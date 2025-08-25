
import { APIPrivate,APIRefresh } from "../../config/axiosconfig";
 async function checkAuth() {
   const response = await APIPrivate.post("/api/auth/checkauth");
    return response.data;
}

 async function refreshAccessToken() {
    const response = await APIRefresh.post("api/auth/refresh-token");
    return response.data; 
}
export{refreshAccessToken,checkAuth}