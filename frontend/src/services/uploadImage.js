import axios from 'axios';
import CryptoJS from 'crypto-js';

const CLOUDINARY_URL = 'https://api.cloudinary.com/v1_1/dei4qqeac/image/upload';

const UPLOAD_PRESET = 'imageMessage';
const API_KEY = '743737443653411';
const API_SECRET = 'r4Tlu-X7bRl4C7eY71QwP-pM83w'; // Đảm bảo đây là bí mật API đầy đủ

export async function uploadImage(file) {
  try {
    const timestamp = Math.round(new Date().getTime() / 1000); 
    const signature = generateSignature(timestamp, UPLOAD_PRESET, API_SECRET);

    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', UPLOAD_PRESET);
    formData.append('timestamp', timestamp);
    formData.append('signature', signature);
    formData.append('api_key', API_KEY);

    const response = await axios.post(CLOUDINARY_URL, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    if (response.data && response.data.secure_url) {
      return response.data.secure_url;
    } else {
      throw new Error('Upload failed: No URL returned');
    }
  } catch (error) {
    const errorMessage = error.response?.data?.error?.message || error.message;
    throw new Error(`Error uploading image to Cloudinary: ${errorMessage}`);
  }
}

function generateSignature(timestamp, uploadPreset, apiSecret) {
  const params = `timestamp=${timestamp}&upload_preset=${uploadPreset}${apiSecret}`;
  return CryptoJS.SHA1(params).toString(CryptoJS.enc.Hex); // Đảm bảo trả về định dạng Hex
}