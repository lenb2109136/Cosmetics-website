import { useState, useEffect } from "react";
import { login } from "../../services/auth/auth";
import { useNavigate } from "react-router-dom";
import { subscribeUser } from "../../pushNotification";
export default function LoginPage() {
    
    
    const navigate=useNavigate()
    const [isSignIn, setIsSignIn] = useState(false);

    useEffect(() => {
        
        const timer = setTimeout(() => setIsSignIn(true), 200);
        return () => clearTimeout(timer);
        
    }, []);
    const [inforlogin, setinforlogin] = useState({
        soDienThoai: "",
        passWord: ""
    })
    const toggle = () => setIsSignIn(!isSignIn);

    return (
        <div className={`container min-h-screen relative overflow-hidden
      ${isSignIn ? "sign-in" : "sign-up"}`}>
            <div className="flex flex-wrap h-screen">
                <div className="w-1/2 flex flex-col items-center justify-center sign-up">
                    <div className="form-wrapper max-w-lg w-full flex items-center justify-center">
                        <form className={`form p-4 bg-white rounded-3xl shadow-lg
              transition-transform duration-500 ease-in-out
              ${isSignIn ? "scale-0 delay-[1000ms]" : "scale-100 delay-0"}`}>
                            <div className="input-group relative my-4">
                                <i className="bx bxs-user absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-500 text-xl"></i>
                                <input
                                    type="text"
                                    placeholder="Username"
                                    className="w-full pl-12 py-4 bg-gray-100 rounded-md border border-white focus:border-green-600 outline-none"
                                />
                            </div>
                            <div className="input-group relative my-4">
                                <i className="bx bx-mail-send absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-500 text-xl"></i>
                                <input
                                    type="email"
                                    placeholder="Email"
                                    className="w-full pl-12 py-4 bg-gray-100 rounded-md border border-white focus:border-green-600 outline-none"
                                />
                            </div>
                            <div className="input-group relative my-4">
                                <i className="bx bxs-lock-alt absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-500 text-xl"></i>
                                <input
                                    type="password"
                                    placeholder="Password"
                                    className="w-full pl-12 py-4 bg-gray-100 rounded-md border border-white focus:border-green-600 outline-none"
                                />
                            </div>
                            <div className="input-group relative my-4">
                                <i className="bx bxs-lock-alt absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-500 text-xl"></i>
                                <input
                                    type="password"
                                    placeholder="Confirm password"
                                    className="w-full pl-12 py-4 bg-gray-100 rounded-md border border-white focus:border-green-600 outline-none"
                                />
                            </div>
                            <button
                                type="submit"
                                className="w-full py-3 rounded-md bg-green-600 text-white text-xl cursor-pointer"
                            >
                                Sign up
                            </button>
                            <p className="mt-4 text-sm">
                                <span>Already have an account? </span>
                                <b
                                    onClick={toggle}
                                    className="cursor-pointer text-green-600 hover:underline"
                                >
                                    Sign in here
                                </b>
                            </p>
                        </form>
                    </div>
                </div>
                <div className="w-1/2 flex flex-col items-center justify-center sign-in">
                    <div className="form-wrapper max-w-lg w-full flex items-center justify-center">
                        <form className={`max-w-xl form p-4 bg-white rounded-3xl shadow-lg
              transition-transform duration-500 ease-in-out
              ${isSignIn ? "scale-100 delay-0" : "scale-0 delay-[1000ms]"}`}>
                            <div className="input-group relative my-4">
                                <i className="bx bxs-user absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-500 text-xl"></i>
                                <input
                                    value={inforlogin.passWord}
                                    onChange={(e) => {
                                        setinforlogin({
                                            ...inforlogin,
                                            passWord: e.target.value
                                        });
                                    }}
                                    type="text"
                                    placeholder="Số điện thoại"
                                    className="w-full pl-12 py-4 bg-gray-100 rounded-md border border-white focus:border-green-600 outline-none"
                                />
                            </div>
                            <div className="input-group relative my-4">
                                <i className="bx bxs-lock-alt absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-500 text-xl"></i>
                                <input
                                    value={inforlogin.soDienThoai}
                                    onChange={(e) => {
                                        setinforlogin({
                                            ...inforlogin,
                                            soDienThoai: e.target.value
                                        });
                                    }}
                                    type="password"
                                    placeholder="Mật khẩu"
                                    className="w-full pl-12 py-4 bg-gray-100 rounded-md border border-white focus:border-green-600 outline-none"
                                />
                            </div>
                            <button onClick={async (events) => {
                                events.preventDefault()
                                try {
                                    const t=await login(inforlogin);
                                    if(t=="ADMIN"){
                                        navigate("/admin")
                                    }
                                    else if(t=="CUSTOMER"){
                                       navigate("/customer")
                                    }
                                    else{
                                         navigate("/employee")
                                    }
                                } catch (error) {
                                    alert(error);
                                }

                            }}
                                type="submit"
                                className="w-full py-3 rounded-md bg-green-600 text-white text-xl cursor-pointer"
                            >
                                Sign in
                            </button>
                            <p className="mt-2 text-sm text-gray-600 cursor-pointer hover:underline">
                                Forgot password?
                            </p>
                            <p className="mt-4 text-sm">
                                <span>Don't have an account? </span>
                                <b
                                    onClick={toggle}
                                    className="cursor-pointer text-green-600 hover:underline"
                                >
                                    Sign up here
                                </b>
                            </p>
                        </form>
                    </div>
                </div>
            </div>
            <div className="content-row absolute top-0 left-0 w-full pointer-events-none z-10 flex h-screen">
                <div className="w-1/2 flex flex-col items-center justify-center text-white m-16">
                    <h2
                        className={`text-5xl font-extrabold mb-8 transition-transform duration-1000 ease-in-out
              ${isSignIn ? "translate-x-0" : "-translate-x-[250%]"}`}
                    >
                        Welcome
                    </h2>
                </div>
                <div className="w-1/2 flex flex-col items-center justify-center text-white m-16">
                    <h2
                        className={`text-5xl font-extrabold mb-8 transition-transform duration-1000 ease-in-out
              ${isSignIn ? "translate-x-[250%]" : "translate-x-0"}`}
                    >
                        Join with us
                    </h2>
                </div>
            </div>
            <div
                className={`absolute top-0 right-0 h-screen w-[300vw] z-0 rounded-[50vw] bg-gradient-to-br from-green-600 to-green-400 shadow-lg transition-transform duration-1000 ease-in-out
          ${isSignIn ? "translate-x-0 right-1/2" : "translate-x-[100%] right-1/2"}`}
                style={{ borderBottomRightRadius: "50vw", borderTopLeftRadius: "50vw" }}
            />
        </div>
    );
}