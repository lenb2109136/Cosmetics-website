import { useEffect, useState } from "react";
import { useFetcher } from "react-router-dom";
import { getallNenDa, getallstep } from "../../services/trangthaiservice";

function CreateRoutine() {
    const routine = {
        buoiSang: {

        },
        buoiTrua: {

        },
        buoiToi: {

        }
    }
    const [buoiHienTai, setBuoiHienTai] = useState(0)
    const [danhSachNenDa,setDanhSachNenDa]=useState([])
    const [pickNenDa,setPickNenDa]=useState()
    const [danhSachStep,setDanhSachStep]=useState([])
    useEffect(()=>{
        getallNenDa().then(d=>setDanhSachNenDa(d))
        getallstep().then(d=>setDanhSachStep(d))
    },[])
    return <div>
        <p>
            hãy xây dựng Routine của bạn
        </p>
        <div className="flex">
            <div class>
                
                <p>Tên routine của bạn</p>
                <input className="border-gray-100 border outline-none" type="text"></input>
                <p>Thêm các tag quen thuộc để người dùng dễ nhận biết</p>
                <input  className="border-gray-100 border outline-none" type="text"></input>
                <p>Mô tả về routine của bạn</p>
                <input  className="border-gray-100 border outline-none" type="text"></input>
                <p>Chọn bức ảnh đại diện cho routine của bạn</p>
                <input  className="border-gray-100 border outline-none" type="text"></input>
            </div>
            <div >
                {/* nền da */}

            {
                danhSachNenDa?.map((d)=>{
                    return <div>
                        <p>{d?.ten}</p>
                    </div>
                })
            }

            </div>
        </div>
        <div>
            <p>Buổi sáng</p>
            <p>+ Thêm bước cho routine buổi sáng</p>
            
        </div>
        <div>
            <p>Buổi Trưa</p>
        </div>
        <div>
            <p>Buổi tối</p>
        </div>
    </div>
}
export { CreateRoutine };