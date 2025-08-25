import { useEffect, useRef } from "react";
import { BarcodeScanner } from "dynamsoft-javascript-barcode";

// Cấu hình Dynamsoft
BarcodeScanner.engineResourcePath = "/dbr/";
BarcodeScanner.license = "DLS2eyJoYW5kc2hha2VDb2RlIjoiMTA0MjIxNDA2LU1UQTBNakl4TkRBMkxYZGxZaTFVY21saGJGQnliMm8iLCJtYWluU2VydmVyVVJMIjoiaHR0cHM6Ly9tZGxzLmR5bmFtc29mdG9ubGluZS5jb20iLCJvcmdhbml6YXRpb25JRCI6IjEwNDIyMTQwNiIsInN0YW5kYnlTZXJ2ZXJVUkwiOiJodHRwczovL3NkbHMuZHluYW1zb2Z0b25saW5lLmNvbSIsImNoZWNrQ29kZSI6MTYzNDE3MDIxOX0=";

const OCRScanner = () => {
  const scannerRef = useRef(null);
  const scannerInstanceRef = useRef(null);
  const hasAlertedRef = useRef(false); // để không alert liên tục nhiều lần

  useEffect(() => {
    const initScanner = async () => {
      try {
        await new Promise((resolve) => requestAnimationFrame(resolve));

        if (!scannerRef.current) {
          console.warn("⚠️ scannerRef không tồn tại.");
          return;
        }

        const scanner = await BarcodeScanner.createInstance();
        scannerInstanceRef.current = scanner;

        await scanner.setUIElement(scannerRef.current);
        console.log("✅ Gắn UI scanner thành công");

        scanner.onFrameRead = (results) => {
          if (results.length > 0 && !hasAlertedRef.current) {
            const resultText = results[0].barcodeText;
            alert("📦 Mã vạch: " + resultText);
            hasAlertedRef.current = true;

            // Nếu bạn muốn dừng scanner sau khi quét:
            // scannerInstanceRef.current.close();
          }
        };

        await scanner.open();
        console.log("📸 Scanner đã hoạt động!");
      } catch (error) {
        console.error("❌ Lỗi khởi tạo Dynamsoft:", error);
      }
    };

    initScanner();

    return () => {
      if (scannerInstanceRef.current) {
        scannerInstanceRef.current.close();
        scannerInstanceRef.current = null;
        hasAlertedRef.current = false;
        console.log("🛑 Đã đóng scanner");
      }
    };
  }, []);

  return (
    <div>
      <h2 className="font-bold text-lg mb-2">Quét mã vạch</h2>
      <div
        ref={scannerRef}
        style={{
          width: "100%",
          height: "400px",
          minHeight: "300px",
          border: "2px dashed green",
        }}
      >
        <div className="dce-video-container" style={{ width: "100%", height: "100%" }}>
          <video className="dce-video" playsInline={true}></video>
          <canvas className="dce-canvas"></canvas>
        </div>
      </div>
    </div>
  );
};

export { OCRScanner };
