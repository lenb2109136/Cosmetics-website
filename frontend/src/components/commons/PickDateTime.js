import React, { useRef, useEffect, useState } from 'react';

// Hàm chuẩn hóa định dạng ngày cho Spring Boot
const formatForSpring = (date) => {
  return date.toISOString().split('.')[0]; // yyyy-MM-ddTHH:mm:ss
};

const DateTimePicker = ({ startDate, endDate, setStartDate, setEndDate }) => {
  const [selectedOption, setSelectedOption] = useState('Trong 30 ngày qua');
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef();

  const today = new Date(); // Use current date instead of hardcoded

  const handleOptionChange = (option) => {
    setSelectedOption(option);
    setDropdownOpen(false);

    let start, end;

    switch (option) {
      case 'Hôm nay':
        start = new Date(today);
        start.setHours(0, 0, 0, 0); // Start of day
        end = new Date(today);
        end.setHours(23, 59, 59, 999); // End of day
        break;
      case 'Hôm qua':
        start = new Date(today);
        start.setDate(today.getDate() - 1);
        start.setHours(0, 0, 0, 0);
        end = new Date(start);
        end.setHours(23, 59, 59, 999);
        break;
      case 'Trong 7 ngày qua':
        start = new Date(today);
        start.setDate(today.getDate() - 6);
        start.setHours(0, 0, 0, 0);
        end = new Date(today);
        end.setHours(23, 59, 59, 999);
        break;
      case 'Trong 30 ngày qua':
        start = new Date(today);
        start.setDate(today.getDate() - 29);
        start.setHours(0, 0, 0, 0);
        end = new Date(today);
        end.setHours(23, 59, 59, 999);
        break;
      default:
        return;
    }

    setStartDate(start);
    setEndDate(end);
  };

  const handleStartDateChange = (e) => {
    const newStartDate = new Date(e.target.value);
    if (newStartDate <= endDate) {
      setStartDate(newStartDate);
      setSelectedOption('Tùy chỉnh'); // Custom selection
    }
  };

  const handleEndDateChange = (e) => {
    const newEndDate = new Date(e.target.value);
    if (newEndDate >= startDate) {
      setEndDate(newEndDate);
      setSelectedOption('Tùy chỉnh'); // Custom selection
    }
  };

  const handleClickOutside = (e) => {
    if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
      setDropdownOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div className="relative w-full max-w-xs font-sans" ref={dropdownRef}>
      <button
        onClick={() => setDropdownOpen(!dropdownOpen)}
        className="w-full px-4 py-2 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-100 text-left text-gray-700 focus:outline-none   transition"
      >
        {selectedOption}
      </button>

      {dropdownOpen && (
        <div className="absolute z-50 mt-2 w-80 bg-white border border-gray-300 rounded-lg shadow-lg p-4">
          {/* <div
            onClick={() => handleOptionChange('Hôm nay')}
            className="cursor-pointer px-3 py-2 hover:bg-blue-50 hover:text-blue-500 rounded transition"
          >
            Hôm nay
          </div>
          <div
            onClick={() => handleOptionChange('Hôm qua')}
            className="cursor-pointer px-3 py-2 hover:bg-blue-50 hover:text-blue-500 rounded transition"
          >
            Hôm qua
          </div> */}
          <div
            onClick={() => handleOptionChange('Trong 7 ngày qua')}
            className="cursor-pointer px-3 py-2 hover:bg-blue-50 hover:text-blue-500 rounded transition"
          >
            Trong 7 ngày qua
          </div>
          <div
            onClick={() => handleOptionChange('Trong 30 ngày qua')}
            className="cursor-pointer px-3 py-2 hover:bg-blue-50 hover:text-blue-500 rounded transition"
          >
            Trong 30 ngày qua
          </div>

          <hr className="my-3 border-gray-200" />
          <div className="font-semibold text-gray-700 mb-2">Tùy chỉnh ngày</div>

          <div className="flex flex-col gap-2">
            <label className="text-gray-700 text-sm">Từ ngày:</label>
            <input
              type="datetime-local"
              className="border border-gray-300 rounded-md px-2 py-1 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={formatForSpring(startDate)}
              onChange={handleStartDateChange}
            />

            <label className="text-gray-700 text-sm">Đến ngày:</label>
            <input
              type="datetime-local"
              className="border border-gray-300 rounded-md px-2 py-1 text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={formatForSpring(endDate)}
              onChange={handleEndDateChange}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default DateTimePicker;