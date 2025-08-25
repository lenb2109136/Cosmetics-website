import { useEffect, useState, useRef } from "react";
import { getAll } from "../../services/ThuongHieuService";
import { useNavigate } from "react-router-dom";
import gsap from "gsap";
import ScrollTrigger from "gsap/ScrollTrigger";

gsap.registerPlugin(ScrollTrigger);

function ListThuongHieu() {
  const [data, setData] = useState([]);
  const itemRefs = useRef([]);
  const navigate = useNavigate();
  const [chuCai,setChuCai]=useState("")

  useEffect(() => {
    getAll(chuCai).then((d) => {
      setData(d);
    });
  }, [chuCai]);

  useEffect(() => {
  if (data.length === 0) return;
  // Dùng requestAnimationFrame để chắc chắn DOM đã gán ref xong
  requestAnimationFrame(() => {
    gsap.fromTo(
      itemRefs.current,
      { scale: 0.5, opacity: 0 },
      {
        scale: 1,
        opacity: 1,
        duration: 0.6,
        ease: "back.out(1.7)",
        stagger: 0.15,
      }
    );
  });
}, [data]);


  return (
    <div>

<div className="flex flex-wrap gap-2 mt-2 pl-4">
   <p onClick={()=> setChuCai("")} key={"all"} className="px-3 py-2 border rounded text-green-900 font-semibold cursor-pointer">
      ALL
    </p>
  {"ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("").map((char) => (
    <p onClick={()=> setChuCai(char)} key={char} className="px-3 py-2 border rounded text-green-900 font-semibold cursor-pointer">
      {char}
    </p>
  ))}
</div>


<div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6 p-4">
      
  {data?.map((d, index) => (
    <div
      key={d.id}
      ref={(el) => (itemRefs.current[index] = el)}
      onClick={() => navigate("/customer/thuonghieu?id=" + d.id)}
      className="w-full h-48 bg-white shadow-md rounded-lg overflow-hidden flex flex-col items-center justify-center cursor-pointer"
    >
      <img
        src={d.anhBia || d.anhDaiDien}
        alt={d.ten}
        className="w-full h-32 object-cover"
      />
      <p className="text-center text-sm font-semibold mt-2">{d.ten}</p>
    </div>
  ))}
</div>

    </div>
    
  );
}

export { ListThuongHieu };
