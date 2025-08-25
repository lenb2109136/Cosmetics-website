import React from "react";
import "../../styles/Loading.css"

function Loading() {
  

  return <div className="fixed inset-0 h-screen w-screen backdrop-blur-2 bg-black/30 z-40 flex justify-center items-center">
<div class="loader">
  <div class="box box-1">
    <div class="side-left"></div>
    <div class="side-right"></div>
    <div class="side-top"></div>
  </div>
  <div class="box box-2">
    <div class="side-left"></div>
    <div class="side-right"></div>
    <div class="side-top"></div>
  </div>
  <div class="box box-3">
    <div class="side-left"></div>
    <div class="side-right"></div>
    <div class="side-top"></div>
  </div>
  <div class="box box-4">
    <div class="side-left"></div>
    <div class="side-right"></div>
    <div class="side-top"></div>
  </div>
</div>
  </div>;
}

export default Loading;
