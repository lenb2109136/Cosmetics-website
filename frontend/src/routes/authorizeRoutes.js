import { checkAuth } from "../services/auth/auth";
import React, { useState, useEffect } from "react";
import { Navigate } from "react-router-dom";

export default function ProtectedRoutes({ children }) {
  const [loading, setLoading] = useState(true);
  const [isAuth, setIsAuth] = useState(false);

  useEffect(() => {
    checkAuth()
      .then(() => {
        setIsAuth(true);
      })
      .catch(() => {
        setIsAuth(false);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading...</div>;

  if (!isAuth) return <Navigate to="/login" replace />;

  return <>{children}</>;
}
