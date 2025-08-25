import React from 'react';

function Typing({ten=""}) {
    return (
        <div className='flex items-center'>
            <div className="flex gap-1 items-end h-5 mt-2 ml-2 mb-1">
                <span className="w-1 h-1 bg-gray-400 rounded-full animate-[typing_1.2s_ease-in-out_infinite] [animation-delay:-0.4s]"></span>
                <span className="w-1 h-1 bg-gray-500 rounded-full animate-[typing_1.2s_ease-in-out_infinite] [animation-delay:-0.2s]"></span>
                <span className="w-1 h-1 bg-gray-400 rounded-full animate-[typing_1.2s_ease-in-out_infinite]"></span>

                <style>{`
        @keyframes typing {
          0%, 100% { transform: translateY(0); }
          50% { transform: translateY(-4px); }
        }
      `}</style>
            </div>
            <p className='text-gray-400 mt-[6px] ml-4'>{ten} đang soạn tin</p>
        </div>
    );
}

export { Typing };
