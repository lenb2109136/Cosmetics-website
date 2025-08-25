import { useParams, useSearchParams } from "react-router-dom";
import { ViewPhieuKiem } from "../admin/viewPhieuKiem";
import { updatePhieuKiem } from "../../services/PhieuNhapService";

function UpdatePhieuKiem(){
    const {id}=useParams();
    const [searchParams] = useSearchParams();
    const cui = searchParams.get("cui");
    if(cui===1){
        return <UpdatePhieuKiem id={id}></UpdatePhieuKiem>
    }
    else{
         return <ViewPhieuKiem id={id}></ViewPhieuKiem>
    }
}
export {updatePhieuKiem}