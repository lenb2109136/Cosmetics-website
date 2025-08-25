import { useEffect, useLayoutEffect, useState, useRef } from "react";
import { layThongSo } from "../../services/ThongSoService";
import { gsap } from "gsap";

export default function PickDetailCategory({ product, id,open,setl,l }) {
  const [danhsachthongso, setdanhsachthongso] = useState([]);
  const [indexPick, setIndexPick] = useState(0);
  const thongsocutheRef = useRef(null);

 useEffect(() => {
  if(open==false&&id!=-1){
    layThongSo(id)
    .then((data) => {
      setdanhsachthongso(data);
      setIndexPick(0);
    })
    .catch((err) => {
    });
  }
}, [id,open]);


  useLayoutEffect(() => {
  const items = thongsocutheRef.current?.children;
  if (items && items.length > 0) {
    gsap.fromTo(
      items,
      { opacity: 0 },
      { opacity: 1, duration: 0.5, stagger: 0.12, ease: "power3.out" }
    );
  }
}, [indexPick, danhsachthongso]);


  const isSelected = (itemId) => {
    return product?.thongSo?.some((item) => item.id === itemId);
  };

 const toggleSelect = (item, element) => {
  if (isSelected(item.id)) {
    product.thongSo=(product.thongSo.filter((i) => i.id !== item.id));
    setl(!l)
    gsap.to(element, {
      opacity: 0.5,
      duration: 0.3,
      ease: "power2.out",
    });
  } else {
    product.thongSo=([...product.thongSo, item]);
setl(!l)
    gsap.fromTo(
      element,
      { opacity: 0.5 },
      {
        opacity: 1,
        duration: 0.3,
        ease: "power2.out",
      }
    );
  }
};


  useEffect(() => {
    const items = thongsocutheRef.current?.children;
    if (!items) return;

    for (const el of items) {
      const itemId = el.getAttribute("data-id");
      const selected = isSelected(Number(itemId));
      gsap.set(el, {
        scale: selected ? 1.05 : 1,
        backgroundColor: selected ? "#3b82f6" : "#f3f4f6",
        color: selected ? "#fff" : "#374151",
        boxShadow: selected
          ? "0 4px 8px rgba(59, 130, 246, 0.5)"
          : "none",
      });
    }
  }, [product.thongSo, indexPick]);

 return (
  <div className="w-full p-6 bg-white rounded-xl shadow-lg">
    <div className="thongso flex items-center gap-4">
      <p className="text-lg font-semibold text-gray-800">Chọn thông số:</p>
      <select
        className="cursor-pointer bg-white border border-gray-200 text-gray-800 text-sm rounded-lg focus:ring-2 focus:ring-blue-400 focus:border-blue-400 block w-64 p-3 transition-all duration-300 hover:shadow-md"
        value={indexPick}
        onChange={(e) => setIndexPick(Number(e.target.value))}
        disabled={danhsachthongso.length === 0} 
      >
        {danhsachthongso.length === 0 ? (
          <option value={-1}>Không có thông số</option>
        ) : (
          danhsachthongso.map((data, index) => (
            <option key={index} value={index}>
              {data.ten}
            </option>
          ))
        )}
      </select>
    </div>

    <div
      ref={thongsocutheRef}
      className="thongsocuthe mt-6 flex flex-wrap gap-4"
    >
      {danhsachthongso.length === 0 ? (
        <div className="text-gray-500 italic">Không có thông số cần chọn</div>
      ) : (
        (danhsachthongso[indexPick]?.thongSoCuThe?.length ?? 0) === 0 ? (
          <div className="text-gray-500 italic">Không có thông số cần chọn</div>
        ) : (
          danhsachthongso[indexPick].thongSoCuThe.map((item) => (
            <div
              key={item.id}
              data-id={item.id}
              className={`cursor-pointer border rounded-lg p-3 text-center w-[23%] transition-all duration-300 
                ${
                  isSelected(item.id)
                    ? "bg-blue-500 text-white shadow-md"
                    : "bg-gray-100 text-gray-800 hover:bg-gray-200 hover:shadow-sm"
                }`}
              onClick={(e) => toggleSelect(item, e.currentTarget)}
            >
              {item.ten}
            </div>
          ))
        )
      )}
    </div>
  </div>
);

}
