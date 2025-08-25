import React from 'react';

const RenderImage = ({ images, cols = 4 }) => {
  const getGridStyle = (length, customCols) => {
    if (length === 1) return 'grid-cols-1';
    if (length === 2) return 'grid-cols-2';
    if (length === 3) return 'grid-cols-2 md:grid-cols-3';
    return `grid-cols-2 md:grid-cols-${customCols > length ? length : customCols} lg:grid-cols-${customCols}`;
  };

  return (
    <div className={`grid gap-2 ${getGridStyle(images.length, cols)}`}>
      {images.map((image, index) => (
        <div key={index} className="relative">
          <img src={image} alt={`Image ${index}`} className="w-full h-auto rounded-lg" />
          {images.length > 1 && (
            <span className="absolute top-2 right-2 bg-gray-800 text-white text-xs px-1 rounded">
              HD
            </span>
          )}
        </div>
      ))}
    </div>
  );
};

function renderImageToHTML(images, cols = 4) {
  if (!images || images.length === 0) return "";

  const length = images.length;

  let gridClass = "grid gap-2 ";
  if (length === 1) {
    gridClass += "grid-cols-1";
  } else if (length === 2) {
    gridClass += "grid-cols-2";
  } else if (length === 3) {
    gridClass += "grid-cols-2 md:grid-cols-3";
  } else {
    const actualCols = cols > length ? length : cols;
    gridClass += `grid-cols-2 md:grid-cols-${actualCols} lg:grid-cols-${cols}`;
  }

  const html =
    `<div class="${gridClass}">` +
    images.map((image, index) => `
      <div class="relative">
        <img 
          src="${image}" 
          alt="Image ${index}" 
          class="w-full h-auto rounded-lg"
        />
        ${length > 1
          ? `<span class="absolute top-2 right-2 bg-gray-800 text-white text-xs px-1 rounded">HD</span>`
          : ""
        }
      </div>
    `).join('') +
    `</div>`;

  return html;
}

export  {RenderImage,renderImageToHTML};