export default function Modal({ children, setOpen, b=true }) {
    return (
        <div
            onClick={() => {if(b==true){setOpen(false)}}}
            className="fixed inset-0 h-screen w-screen  bg-black/30 z-40 flex justify-center items-center"
        >
            <div
                onClick={(e) => e.stopPropagation()}
                className="w-fit max-w-[90%] max-h-[90vh] bg-white rounded-sm p-4 overflow-y-auto"
            >
                {children}
            </div>
        </div>
    );
}