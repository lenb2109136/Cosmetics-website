import { APIRefresh, APIPublic } from "../../config/axiosconfig";
import { refreshAccessToken } from "../../api/auth/authAPI";
import { checkAuth as apiCheckAuth } from "../../api/auth/authAPI";
import { API_ROUTES } from "../../utils/constants";
import AppRouter from "../../routes/AppRoutes";
import { info } from "autoprefixer";

async function checkAuth() {
  let token = localStorage.getItem("token");
  console.log(token)
  if (!token) {
    try {
      await refreshToken();
    } catch {
      throw new Error("UNAUTHENTICATED");
    }
  } else {
    try {
      await apiCheckAuth();
    } catch(error) {
      console.log(error)
      try {
        await refreshToken();
      } catch {
        throw new Error("UNAUTHENTICATED");
      }
    }
  }
}

async function refreshToken() {
  let refresh = await refreshAccessToken();
  saveInforLogin(refresh);
  return refresh;
}

let refreshTimerId = null;

function saveInforLogin(response) {
  const loginData = response.data;
  const token = loginData.accessToken;
  const expiresIn = loginData.expiresIn;
  const user = loginData.user;

  localStorage.setItem("token", token);
    localStorage.setItem("id", user.id);
  localStorage.setItem("name", user.fullName);
  localStorage.setItem("roles", JSON.stringify(user.role));

  const expiryTime = Date.now() + expiresIn * 1000;
  localStorage.setItem("tokenExpiry", expiryTime);

  if (refreshTimerId) clearTimeout(refreshTimerId);

  refreshTimerId = setTimeout(() => {
    refreshToken().catch(() => { });
  }, (expiresIn - 40) * 1000);
}

function setupAutoRefreshToken() {
  const expiryTime = localStorage.getItem("tokenExpiry");
  if (!expiryTime) return;

  const expiresInMs = expiryTime - Date.now();
  if (expiresInMs <= 30000) {
    refreshToken().catch(() => { });
  } else {
    refreshTimerId = setTimeout(() => {
      refreshToken().catch(() => { });
    }, expiresInMs - 30000);
  }
}

setupAutoRefreshToken();
async function login(infor) {
  try {
    let response = await APIRefresh.post(API_ROUTES.LOGIN, infor)
    saveInforLogin(response.data)
    let roles = response?.data?.data?.user?.role || [];

    let roleResult = "";
    if (roles.includes("ADMIN")) {
      roleResult = "ADMIN";
    } else if (roles.includes("CUSTOMER")) {
      roleResult = "CUSTOMER";
    } else if (roles.includes("EMPLOYEE")) {
      roleResult = "EMPLOYEE";
    }
    return roleResult;
  }
  catch (error) {
    throw error;
  }

}

export { refreshToken, checkAuth, login };
