import { useEffect, useRef } from "react";
import gsap from "gsap";
import ScrollTrigger from "gsap/ScrollTrigger";
import ScrollSmoother from "gsap/ScrollSmoother";

import BackGroundTopLeft from "../../assets/background.png";
import BackGroundBotRight from "../../assets/backgoundleft.png";
import Landing1 from "../../assets/landingpage.webp";
import Landing2 from "../../assets/landing2.png";
import Landing3 from "../../assets/landing3.png";
import { useNavigate } from "react-router-dom";

gsap.registerPlugin(ScrollTrigger, ScrollSmoother);

function LandingPage() {
  const imglan1 = useRef(null);
  const navigate= useNavigate()
  const text1 = useRef(null);
  const img2Ref = useRef(null);
  const img3Ref = useRef(null);
  const topRightLeaf = useRef(null);
  const bottomLeftLeaf = useRef(null);

  useEffect(() => {
    const smoother = ScrollSmoother.create({
      wrapper: "#smooth-wrapper",
      content: "#smooth-content",
      smooth: 1.5,
      effects: true,
    });

    // 🌿 Hiệu ứng xuất hiện ban đầu cho 2 lá
    gsap.fromTo(
      topRightLeaf.current,
      { x: 200, y: -100, opacity: 0 },
      { x: 0, y: 0, opacity: 1, duration: 1.2, ease: "power2.out" }
    );

    gsap.fromTo(
      bottomLeftLeaf.current,
      { x: -100, y: 150, opacity: 0 },
      { x: 0, y: 0, opacity: 1, duration: 1.2, delay: 0.3, ease: "power2.out" }
    );

    // 🌿 Scroll: Lá trên phải di chuyển ra, nhỏ lại, mờ dần
    gsap.to(topRightLeaf.current, {
      x: 150,
      y: 150,
      scale: 0.5,
      opacity: 0,
      ease: "none",
      scrollTrigger: {
        trigger: ".section2-trigger",
        start: "top 95%",     // bắt đầu sớm hơn
        end: "top 5%",        // kết thúc muộn hơn
        scrub: true,
      },
    });

    // 🌿 Scroll: Lá dưới trái di chuyển ra, nhỏ lại, mờ dần
    gsap.to(bottomLeftLeaf.current, {
      x: -100,
      y: 100,
      scale: 0.5,
      opacity: 0,
      ease: "none",
      scrollTrigger: {
        trigger: ".section2-trigger",
        start: "top 95%",
        end: "top 5%",
        scrub: true,
      },
    });

    // 📸 Zoom ảnh đầu trang
    if (imglan1.current) {
      gsap.fromTo(
        imglan1.current,
        { scale: 1.5 },
        { scale: 1, duration: 1.5, ease: "power2.out" }
      );
    }

    // ✨ Text hiệu ứng từ trái vào
    if (text1.current) {
      gsap.fromTo(
        text1.current,
        { x: -100, opacity: 0 },
        { x: 0, opacity: 1, duration: 1.2, delay: 0.5, ease: "power2.out" }
      );
    }

    // 🧹 Cleanup
    return () => {
      smoother.kill();
      ScrollTrigger.getAll().forEach((trigger) => trigger.kill());
    };
  }, []);

  return (
    <div id="smooth-wrapper" className="relative overflow-hidden">
      {/* 🌿 Lá trên phải */}
      <img
        ref={topRightLeaf}
        src={BackGroundTopLeft}
        alt="bg"
        className="absolute top-0 right-0 w-48 z-30"
      />
      {/* 🌿 Lá dưới trái */}
      <img
        ref={bottomLeftLeaf}
        src={BackGroundBotRight}
        alt="bg"
        className="absolute bottom-0 left-0 w-48 z-30"
      />

      <div id="smooth-content">
        {/* Section 1 */}
        <section className="relative w-full h-screen bg-white overflow-hidden">
          <img
            src={Landing1}
            alt="Landing"
            ref={imglan1}
            className="absolute top-0 left-0 w-full h-full object-cover z-0"
          />
          <div
            ref={text1}
            data-speed="0.7"
            className="absolute top-1/2 left-14 transform -translate-y-1/2 text-left z-10 drop-shadow-xl"
          >
            <h3 className="text-2xl md:text-3xl italic mb-2">Glow Your Way!</h3>
            <h1 className="text-4xl md:text-6xl font-bold leading-tight mb-4">
              Radiance Crafted<br />for You
            </h1>
            <p className="text-base md:text-lg mb-6 max-w-sm">
              Discover clean, non-toxic beauty products designed to enhance your natural glow.
            </p>
            <button onClick={()=>{navigate("viewproduct")}} className="bg-white text-black px-6 py-3 rounded hover:bg-gray-200 transition">
              Shop the Glow
            </button>
          </div>
        </section>

        {/* Section 2 */}
        <section className="section2-trigger flex flex-col md:flex-row px-8 py-20 gap-8 items-center bg-[#f7f7f7]">
          <div className="basis-1/2">
            <h3 className="text-3xl italic mb-2 text-black">Flawless on Every Screen</h3>
            <h1 className="text-4xl md:text-6xl font-bold leading-tight mb-4 text-black">
              Seamless Shopping <br /> Across Devices
            </h1>
            <p className="text-base md:text-lg text-gray-700 max-w-md">
              Experience seamless browsing with lightning-fast mobile loading. Our sleek, app-like design ensures a clear, focused beauty shopping experience.
            </p>
          </div>
          <div className="basis-1/2">
            <img
              ref={img2Ref}
              src={Landing2}
              alt="Landing 2"
              className="w-full rounded-xl shadow-lg"
            />
          </div>
        </section>

        {/* Section 3 */}
        <section className="flex flex-col md:flex-row px-8 py-20 gap-8 items-center bg-white">
          <div className="basis-1/2">
            <img
              ref={img3Ref}
              src={Landing3}
              alt="Landing 3"
              className="w-full rounded-xl shadow-lg"
            />
          </div>
          <div className="basis-1/2">
            <h3 className="text-3xl italic mb-2 text-black">Glow at Lightning Speed</h3>
            <h1 className="text-4xl md:text-6xl font-bold leading-tight mb-4 text-black">
              Native Performance<br /> Radiant Speed
            </h1>
            <p className="text-base md:text-lg text-gray-700 max-w-md">
              Enjoy a smooth, native-like experience with our optimized framework, delivering radiant performance across all devices.
            </p>
          </div>
        </section>
      </div>
    </div>
  );
}

export { LandingPage };
