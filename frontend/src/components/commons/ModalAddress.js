import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { getTinh, getHuyen, getXa } from "../../services/address";

function ModalAddress({ address, setAddress, addressIds, setAddressIds, setOpen }) {
    const [provinces, setProvinces] = useState([]);
    const [districts, setDistricts] = useState([]);
    const [wards, setWards] = useState([]);
    const [selectedProvince, setSelectedProvince] = useState(addressIds[0] || "");
    const [selectedDistrict, setSelectedDistrict] = useState(addressIds[1] || "");
    const [selectedWard, setSelectedWard] = useState(addressIds[2] || "");
    const [detailedAddress, setDetailedAddress] = useState(
        address.split(", ").slice(-1)[0] || ""
    );
    const [isProvinceOpen, setIsProvinceOpen] = useState(false);
    const [isDistrictOpen, setIsDistrictOpen] = useState(false);
    const [isWardOpen, setIsWardOpen] = useState(false);
    const [initialAddress, setInitialAddress] = useState(address);
    const [initialAddressIds, setInitialAddressIds] = useState([...addressIds]);

    useEffect(() => {
        getTinh()
            .then((data) => {
                setProvinces(data);
                if (addressIds[0]) {
                    const province = data.find((p) => p.code === addressIds[0]);
                    if (province) {
                        setSelectedProvince(province.code);
                        if (province.districts) {
                            setDistricts(province.districts);
                            if (addressIds[1]) {
                                const district = province.districts.find((d) => d.code === addressIds[1]);
                                if (district) setSelectedDistrict(district.code);
                            }
                        }
                    }
                }
            })
            .catch((err) => toast.error("Không thể tải danh sách tỉnh: " + err.message));
    }, [addressIds[0]]);

    useEffect(() => {
        if (selectedProvince) {
            getHuyen(selectedProvince)
                .then((data) => {
                    setDistricts(data);
                    setWards([]);
                    setSelectedDistrict("");
                    setSelectedWard("");
                    if (addressIds[1]) {
                        const district = data.find((d) => d.code === addressIds[1]);
                        if (district) {
                            setSelectedDistrict(district.code);
                            if (district.wards) {
                                setWards(district.wards);
                                if (addressIds[2]) {
                                    const ward = district.wards.find((w) => w.code === addressIds[2]);
                                    if (ward) setSelectedWard(ward.code);
                                }
                            }
                        }
                    }
                })
                .catch((err) => toast.error("Không thể tải danh sách huyện: " + err.message));
        } else {
            setDistricts([]);
            setWards([]);
            setSelectedDistrict("");
            setSelectedWard("");
        }
    }, [selectedProvince, addressIds[1]]);

    useEffect(() => {
        if (selectedDistrict) {
            getXa(selectedDistrict)
                .then((data) => {
                    setWards(data);
                    if (addressIds[2]) {
                        const ward = data.find((w) => w.code === addressIds[2]);
                        if (ward) setSelectedWard(ward.code);
                    }
                })
                .catch((err) => toast.error("Không thể tải danh sách xã: " + err.message));
        } else {
            setWards([]);
            setSelectedWard("");
        }
    }, [selectedDistrict, addressIds[2]]);

    const handleSave = () => {
        if (!selectedProvince || !selectedDistrict || !selectedWard || !detailedAddress) {
            toast.error("Vui lòng điền đầy đủ thông tin địa chỉ.");
            return;
        }

        const provinceName = provinces.find((p) => p.code === selectedProvince)?.name || "";
        const districtName = districts.find((d) => d.code === selectedDistrict)?.name || "";
        const wardName = wards.find((w) => w.code === selectedWard)?.name || "";

        const newAddress = `${provinceName}, ${districtName}, ${wardName}, ${detailedAddress}`;
        setAddress(newAddress);
        setAddressIds([selectedProvince, selectedDistrict, selectedWard]);
        toast.success("Cập nhật địa chỉ thành công!");
        setOpen(false);
    };

    const handleClose = () => {
        setAddress(initialAddress);
        setAddressIds([...initialAddressIds]);
        setSelectedProvince(initialAddressIds[0] || "");
        setSelectedDistrict(initialAddressIds[1] || "");
        setSelectedWard(initialAddressIds[2] || "");
        setDetailedAddress(initialAddress.split(", ").slice(-1)[0] || "");
        setOpen(false);
    };

    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center  " >
            <div className="bg-white rounded-lg p-6 w-full max-w-4xl shadow-lg">
                <div className="flex flex-col space-y-6">
                    <p className="text-lg">Cập nhật thông tin địa chỉ</p>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-900 flex items-center">
                                <i className="fa-solid fa-map-marker-alt mr-2 text-blue-500 bg-blue-100 p-2 rounded-md"></i>
                                Tỉnh/Thành phố
                            </label>
                            <div className="relative mt-2">
                                <button
                                    type="button"
                                    aria-expanded={isProvinceOpen}
                                    aria-haspopup="listbox"
                                    aria-labelledby="province-label"
                                    className="grid w-full cursor-default grid-cols-1 rounded-md bg-white py-1.5 pr-2 pl-3 text-left text-gray-900 outline-1 -outline-offset-1 outline-gray-300 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm"
                                    onClick={() => setIsProvinceOpen(!isProvinceOpen)}
                                >
                                    <span className="col-start-1 row-start-1 flex items-center gap-3 pr-6">
                                        <span className="block truncate">
                                            {provinces.find((p) => p.code === selectedProvince)?.name || "Chọn tỉnh"}
                                        </span>
                                    </span>
                                    <svg
                                        viewBox="0 0 16 16"
                                        fill="currentColor"
                                        aria-hidden="true"
                                        className="col-start-1 row-start-1 size-5 self-center justify-self-end text-gray-500 sm:size-4"
                                    >
                                        <path d="M5.22 10.22a.75.75 0 0 1 1.06 0L8 11.94l1.72-1.72a.75.75 0 1 1 1.06 1.06l-2.25 2.25a.75.75 0 0 1-1.06 0l-2.25-2.25a.75.75 0 0 1 0-1.06ZM10.78 5.78a.75.75 0 0 1-1.06 0L8 4.06 6.28 5.78a.75.75 0 0 1-1.06-1.06l2.25-2.25a.75.75 0 0 1 1.06 0l2.25 2.25a.75.75 0 0 1 0 1.06Z" clip-rule="evenodd" fill-rule="evenodd" />
                                    </svg>
                                </button>
                                {isProvinceOpen && (
                                    <ul
                                        role="listbox"
                                        tabIndex="-1"
                                        aria-labelledby="province-label"
                                        className="absolute z-10 mt-1 max-h-56 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black/5 focus:outline-none sm:text-sm"
                                    >
                                        {provinces.map((province) => (
                                            <li
                                                key={province.code}
                                                role="option"
                                                className="relative cursor-default py-2 pr-9 pl-3 text-gray-900 select-none hover:bg-indigo-600 hover:text-white"
                                                onClick={() => {
                                                    setSelectedProvince(province.code);
                                                    setIsProvinceOpen(false);
                                                }}
                                            >
                                                <div className="flex items-center">
                                                    <span className="ml-3 block truncate font-normal">
                                                        {province.name}
                                                    </span>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </div>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-900 flex items-center">
                                <i className="fa-solid fa-map-marker-alt mr-2 text-blue-500 bg-blue-100 p-2 rounded-md"></i>
                                Huyện/Quận
                            </label>
                            <div className="relative mt-2">
                                <button
                                    type="button"
                                    aria-expanded={isDistrictOpen}
                                    aria-haspopup="listbox"
                                    aria-labelledby="district-label"
                                    className="grid w-full cursor-default grid-cols-1 rounded-md bg-white py-1.5 pr-2 pl-3 text-left text-gray-900 outline-1 -outline-offset-1 outline-gray-300 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm"
                                    onClick={() => setIsDistrictOpen(!isDistrictOpen)}
                                    disabled={!selectedProvince}
                                >
                                    <span className="col-start-1 row-start-1 flex items-center gap-3 pr-6">
                                        <span className="block truncate">
                                            {districts.find((d) => d.code === selectedDistrict)?.name || "Chọn huyện"}
                                        </span>
                                    </span>
                                    <svg
                                        viewBox="0 0 16 16"
                                        fill="currentColor"
                                        aria-hidden="true"
                                        className="col-start-1 row-start-1 size-5 self-center justify-self-end text-gray-500 sm:size-4"
                                    >
                                        <path d="M5.22 10.22a.75.75 0 0 1 1.06 0L8 11.94l1.72-1.72a.75.75 0 1 1 1.06 1.06l-2.25 2.25a.75.75 0 0 1-1.06 0l-2.25-2.25a.75.75 0 0 1 0-1.06ZM10.78 5.78a.75.75 0 0 1-1.06 0L8 4.06 6.28 5.78a.75.75 0 0 1-1.06-1.06l2.25-2.25a.75.75 0 0 1 1.06 0l2.25 2.25a.75.75 0 0 1 0 1.06Z" clip-rule="evenodd" fill-rule="evenodd" />
                                    </svg>
                                </button>
                                {isDistrictOpen && (
                                    <ul
                                        role="listbox"
                                        tabIndex="-1"
                                        aria-labelledby="district-label"
                                        className="absolute z-10 mt-1 max-h-56 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black/5 focus:outline-none sm:text-sm"
                                    >
                                        {districts.map((district) => (
                                            <li
                                                key={district.code}
                                                role="option"
                                                className="relative cursor-default py-2 pr-9 pl-3 text-gray-900 select-none hover:bg-indigo-600 hover:text-white"
                                                onClick={() => {
                                                    setSelectedDistrict(district.code);
                                                    setIsDistrictOpen(false);
                                                }}
                                            >
                                                <div className="flex items-center">
                                                    <span className="ml-3 block truncate font-normal">
                                                        {district.name}
                                                    </span>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </div>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-900 flex items-center">
                                <i className="fa-solid fa-map-marker-alt mr-2 text-blue-500 bg-blue-100 p-2 rounded-md"></i>
                                Xã/Phường
                            </label>
                            <div className="relative mt-2">
                                <button
                                    type="button"
                                    aria-expanded={isWardOpen}
                                    aria-haspopup="listbox"
                                    aria-labelledby="ward-label"
                                    className="grid w-full cursor-default grid-cols-1 rounded-md bg-white py-1.5 pr-2 pl-3 text-left text-gray-900 outline-1 -outline-offset-1 outline-gray-300 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm"
                                    onClick={() => setIsWardOpen(!isWardOpen)}
                                    disabled={!selectedDistrict}
                                >
                                    <span className="col-start-1 row-start-1 flex items-center gap-3 pr-6">
                                        <span className="block truncate">
                                            {wards.find((w) => w.code === selectedWard)?.name || "Chọn xã"}
                                        </span>
                                    </span>
                                    <svg
                                        viewBox="0 0 16 16"
                                        fill="currentColor"
                                        aria-hidden="true"
                                        className="col-start-1 row-start-1 size-5 self-center justify-self-end text-gray-500 sm:size-4"
                                    >
                                        <path d="M5.22 10.22a.75.75 0 0 1 1.06 0L8 11.94l1.72-1.72a.75.75 0 1 1 1.06 1.06l-2.25 2.25a.75.75 0 0 1-1.06 0l-2.25-2.25a.75.75 0 0 1 0-1.06ZM10.78 5.78a.75.75 0 0 1-1.06 0L8 4.06 6.28 5.78a.75.75 0 0 1-1.06-1.06l2.25-2.25a.75.75 0 0 1 1.06 0l2.25 2.25a.75.75 0 0 1 0 1.06Z" clip-rule="evenodd" fill-rule="evenodd" />
                                    </svg>
                                </button>
                                {isWardOpen && (
                                    <ul
                                        role="listbox"
                                        tabIndex="-1"
                                        aria-labelledby="ward-label"
                                        className="absolute z-10 mt-1 max-h-56 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black/5 focus:outline-none sm:text-sm"
                                    >
                                        {wards.map((ward) => (
                                            <li
                                                key={ward.code}
                                                role="option"
                                                className="relative cursor-default py-2 pr-9 pl-3 text-gray-900 select-none hover:bg-indigo-600 hover:text-white"
                                                onClick={() => {
                                                    setSelectedWard(ward.code);
                                                    setIsWardOpen(false);
                                                }}
                                            >
                                                <div className="flex items-center">
                                                    <span className="ml-3 block truncate font-normal">
                                                        {ward.name}
                                                    </span>
                                                </div>
                                            </li>
                                        ))}
                                    </ul>
                                )}
                            </div>
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-900 flex items-center">
                            <i className="fa-solid fa-map-marker-alt mb-2 mr-2 text-blue-500 bg-blue-100 p-2 rounded-md"></i>
                            Địa chỉ chi tiết
                        </label>
                        <input
                            type="text"
                            value={detailedAddress}
                            onChange={(e) => setDetailedAddress(e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded-lg bg-white text-gray-700 focus:outline-none"
                            placeholder="Nhập địa chỉ chi tiết"
                        />
                    </div>
                    <div className="flex justify-end space-x-2">
                        <button
                            onClick={handleClose}
                            className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition duration-200"
                        >
                            Đóng
                        </button>
                        <button
                            onClick={handleSave}
                            className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition duration-200"
                        >
                            Cập nhật
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export { ModalAddress };