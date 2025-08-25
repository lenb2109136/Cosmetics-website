import React, { useState, useEffect } from "react";
import { Navigate } from "react-router-dom";
import { checkAuth } from "../services/auth/auth";
export default function ProtectedRoutes({ children, allowedRoles = [] }) {
  const [loading, setLoading] = useState(true);
  const [isAuth, setIsAuth] = useState(false);
  const [userRoles, setUserRoles] = useState([]);

  useEffect(() => {
    checkAuth()
      .then(() => {
        setIsAuth(true);
        const rolesStr = localStorage.getItem("roles");
        if (rolesStr) {
          try {
            const roles = JSON.parse(rolesStr);
            setUserRoles(roles);
          } catch {
            setUserRoles([]);
          }
        } else {
          setUserRoles([]);
        }
      })
      .catch(() => {
        setIsAuth(false);
        setUserRoles([]);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading...</div>;

  if (!isAuth) return <Navigate to="/login" replace />;
  const hasRole = userRoles.some(role => allowedRoles.includes(role));

  if (!hasRole) return <Navigate to="/unauthorized" replace />;

  return <>{children}</>;
}
