import { useEffect, useRef } from "react";
import gsap from "gsap";

function Pagination({ trangHienTai, setTrangHienTai, soLuongTrang,color="bg-blue-600", textColor="text-white" }) {
  const containerRef = useRef(null);

  useEffect(() => {
    if (!containerRef.current) return;

    const ctx = gsap.context(() => {
      gsap.from(containerRef.current, {
        x: 40,
        opacity: 0,
        duration: 0.8,
        ease: "power2.out",
      });
    }, containerRef);

    return () => ctx.revert();
  }, []);

  const handlePageChange = (newPage) => {
    if (
      newPage >= 0 &&
      newPage < soLuongTrang &&
      newPage !== trangHienTai
    ) {
      setTrangHienTai(newPage);
    }
  };

  const renderPagination = () => {
    if (!soLuongTrang || soLuongTrang <= 0) {
      return <span className="text-gray-500">Không có dữ liệu</span>;
    }

    const pages = [];

    if (soLuongTrang <= 5) {
      for (let i = 0; i < soLuongTrang; i++) {
        pages.push(i);
      }
    } else {
      pages.push(0);

      if (trangHienTai > 2) {
        pages.push("...");
      }

      const start = Math.max(1, trangHienTai - 1);
      const end = Math.min(soLuongTrang - 2, trangHienTai + 1);

      for (let i = start; i <= end; i++) {
        if (!pages.includes(i)) {
          pages.push(i);
        }
      }

      if (trangHienTai < soLuongTrang - 3) {
        pages.push("...");
      }

      if (!pages.includes(soLuongTrang - 1)) {
        pages.push(soLuongTrang - 1);
      }
    }

    return pages.map((page, idx) => {
      if (page === "...") {
        return (
          <span
            key={`dots-${idx}`}
            className="px-2 py-1 text-gray-500 select-none"
          >
            ...
          </span>
        );
      }

      return (
        <button
          key={`page-${page}`}
          onClick={() => handlePageChange(page)}
          className={`px-3 py-1 border rounded hover:bg-gray-100 transition duration-200 ${
            trangHienTai === page
              ? `${color} ${textColor} cursor-default`
              : "text-gray-700"
          }`}
          disabled={trangHienTai === page}
        >
          {page + 1}
        </button>
      );
    });
  };

  return (
    <div
      ref={containerRef}
      className="flex justify-between items-center mt-6"
    >
      <div></div>
      <div className="flex items-center space-x-2">
        <button
          onClick={() => handlePageChange(trangHienTai - 1)}
          className={`px-3 py-1 border rounded transition duration-200 ${
            trangHienTai === 0
              ? "bg-gray-200 text-gray-400 cursor-not-allowed"
              : "hover:bg-gray-100 text-gray-700"
          }`}
          disabled={trangHienTai === 0}
        >
          Trước
        </button>
        {renderPagination()}
        <button
          onClick={() => handlePageChange(trangHienTai + 1)}
          className={`px-3 py-1 border rounded transition duration-200 ${
            trangHienTai >= soLuongTrang - 1
              ? "bg-gray-200 text-gray-400 cursor-not-allowed"
              : "hover:bg-gray-100 text-gray-700"
          }`}
          disabled={trangHienTai >= soLuongTrang - 1}
        >
          Sau
        </button>
      </div>
    </div>
  );
}

export  {Pagination};
