import { useEffect, useRef, useState } from "react"
import { getCategory} from "../../services/CategoryService"

function addContainer(danhSach, danhSachChon, setDanhSach, setDanhSachChon, color="text-red-500") {
    const container = document.getElementsByClassName("containercategory")[0];
    container.innerHTML = "";
    let listHienThi = danhSach;

    for (let i = 0; i <= danhSachChon.length; i++) {
        if (listHienThi.length == 0) {
            break;
        }
        const colCategory = document.createElement("div");
        colCategory.className = `w-[300px] h-[300px] bg-white p-2 overflow-y-auto shadow-md rounded-md mr-2 colCategory${i}`;
        container.appendChild(colCategory);

        let viTriChon = 0;

        for (let u = 0; u < listHienThi.length; u++) {
            const phanTu = document.createElement("div");
            phanTu.className = "flex justify-between items-center px-4 py-2 mb-2 rounded-lg transition duration-200 cursor-pointer hover:bg-gray-100 shadow-sm";

            if (i < danhSachChon.length) {
                if (listHienThi[u].id === danhSachChon[i].id) {
                    phanTu.classList.add(color, "font-semibold", "bg-gray-100");
                    viTriChon = u;
                }
            }

            const spanTen = document.createElement("span");
            const spanIcon = document.createElement("span");
            spanTen.innerHTML = listHienThi[u].ten;
            if (listHienThi[u].danhSach.length > 0) {
                spanIcon.innerHTML = ">";
            }

            phanTu.appendChild(spanTen);
            phanTu.appendChild(spanIcon);

            const currentList = [...listHienThi];
            phanTu.addEventListener("click", () => {
                if (danhSachChon?.length != 0) {
                    const newArray = danhSachChon.slice(0, i);
                    newArray.push(currentList[u]);
                    setDanhSachChon(newArray);
                }
                else {
                    setDanhSachChon([currentList[u]])
                }
            });


            colCategory.appendChild(phanTu);
        }
        listHienThi = listHienThi[viTriChon]?.danhSach || [];
    }
}




export default function PickCategory({ categoryPick, setcategoryPick, setChuoi,setOpen,empty=false,color="text-red-500" }) {
    const [DanhSach, setDanhSach] = useState([])
    const [ok, setok] = useState(false)
    useEffect(() => {
    if (categoryPick?.length !== 0) {
        const lastItem = categoryPick[categoryPick.length - 1];
        if (empty || (lastItem?.danhSach?.length === 0)) {
            setok(true);
        } else {
            setok(false);
        }
    } else {
        setok(false);
    }

    addContainer(DanhSach, categoryPick, setDanhSach, setcategoryPick,color);

    return () => {
        if (Array.isArray(categoryPick) && categoryPick.length > 0) {
            const chuoiTongHop = categoryPick
                .map(item => item?.ten ?? "Không rõ")
                .join(' > ');
            setChuoi(chuoiTongHop);
        } else {
            setChuoi('Không có danh mục nào được chọn');
        }
    };
}, [categoryPick, DanhSach, empty]); 

    useEffect(() => {
        getCategory().then(data=>{
            setDanhSach(data.data)
        }).catch(()=>{
            alert("Có lỗi xảy ra")
        })

    }, []);
    return <div className="w-[1000px]">
        <strong>
            <p className="text-xl ml-5 mt-6">Lựa chọn nhóm sản phẩm</p>
        </strong>
        <div className="ml-5 mt-6 mb-5 p-4 bg-gray-100 w-[95%] rounded-md overflow-x-auto">
            <div className="flex flex-row flex-nowrap min-w-max gap-4 containercategory">
                <div className="w-[300px] h-[300px] bg-white colCategory1"></div>
                <div className="w-[300px] h-[300px] bg-white colCategory2"></div>
                <div className="w-[300px] h-[300px] bg-white colCategory3"></div>
                <div className="w-[300px] h-[300px] bg-white colCategory4"></div>
            </div>

        </div>
        <div className="flex flex-row">
            <div className="basis-3/4">
                <span className="ml-5">
                    Đã chọn: {
                        Array.isArray(categoryPick) && categoryPick.length > 0
                            ? categoryPick.map((data, index) => (
                                <strong key={index}>
                                    <span>{data?.ten ?? "Không rõ"}{index < categoryPick.length - 1 ? ' > ' : ''}</span>
                                </strong>
                            ))
                            : <em>Không có danh mục nào được chọn</em>
                    }
                </span>

            </div>
            <button
            onClick={()=>{
              setOpen(false)
            }}
                disabled={!ok}
                className={`mr-6 basis-1/4 pt-2 pb-2 px-4 rounded-md text-white w-fit transition-colors duration-300
    ${ok ? 'bg-blue-500 hover:bg-blue-600' : 'bg-gray-400 cursor-not-allowed'}
  `}
            >
                <strong>Xác nhận</strong>
            </button>


        </div>
    </div>

}                                                                                                                                                                 