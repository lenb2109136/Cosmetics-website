import axios from "axios";
import { refreshToken } from "../services/auth/auth";
const APIPublic = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 50000,
    
});

const APIPrivate = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 50000,
    
});

const APIRefresh = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 50000,
    withCredentials: true,
});
APIPrivate.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});


let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
    failedQueue.forEach(({ resolve, reject }) => {
        if (error) reject(error);
        else resolve(token);
    });
    failedQueue = [];
};

APIPrivate.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                }).then(token => {
                    originalRequest.headers.Authorization = `Bearer ${token}`;
                    return APIPrivate(originalRequest);
                }).catch(err => Promise.reject(err));
            }

            originalRequest._retry = true;
            isRefreshing = true;

            try {
                const response = await refreshToken();
                const accessToken = response.data.accessToken;
                APIPrivate.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                processQueue(null, accessToken);

                return APIPrivate(originalRequest);
            } catch (err) {
                processQueue(err, null);
                return Promise.reject(err);
            } finally {
                isRefreshing = false;
            }
        }
        return Promise.reject(error);
    }
);


export { APIPublic, APIPrivate, APIRefresh };
