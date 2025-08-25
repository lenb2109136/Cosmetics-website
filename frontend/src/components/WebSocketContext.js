import React, { createContext, useEffect, useRef, useState } from "react";

export const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const socketRef = useRef(null);
  const listenersRef = useRef([]);
  const [isConnected, setIsConnected] = useState(false);
  const reconnectIntervalRef = useRef(null);

  useEffect(() => {
    const connect = () => {
      // Nếu đã có kết nối đang mở, không tạo mới
      if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
        return;
      }

      const socket = new WebSocket("ws://localhost:8080/ws");
      socketRef.current = socket;

      socket.onopen = () => {
        console.log("WebSocket connected");
        setIsConnected(true);
        // Xóa interval khi kết nối thành công
        if (reconnectIntervalRef.current) {
          clearInterval(reconnectIntervalRef.current);
          reconnectIntervalRef.current = null;
        }
      };

      socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        listenersRef.current.forEach((listener) => listener(data));
      };

      socket.onclose = () => {
        console.log("WebSocket closed");
        setIsConnected(false);
        // Bắt đầu thử kết nối lại sau 5 giây
        if (!reconnectIntervalRef.current) {
          reconnectIntervalRef.current = setInterval(connect, 5000);
        }
      };

      socket.onerror = (err) => {
        console.error("WebSocket error:", err);
        setIsConnected(false);
        // Đóng kết nối nếu có lỗi để kích hoạt onclose
        socket.close();
      };
    };

    connect();

    // Cleanup khi component unmount
    return () => {
      if (reconnectIntervalRef.current) {
        clearInterval(reconnectIntervalRef.current);
      }
      if (socketRef.current) {
        socketRef.current.close();
      }
    };
  }, []);

  const sendMessage = (messageObj) => {
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify(messageObj));
    } else {
      console.warn("WebSocket is not connected");
    }
  };

  const addMessageListener = (listener) => {
    listenersRef.current.push(listener);
  };

  const removeMessageListener = (listener) => {
    listenersRef.current = listenersRef.current.filter((l) => l !== listener);
  };

  return (
    <WebSocketContext.Provider
      value={{
        socket: socketRef.current,
        isConnected,
        sendMessage,
        addMessageListener,
        removeMessageListener,
      }}
    >
      {children}
    </WebSocketContext.Provider>
  );
};