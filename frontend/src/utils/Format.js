function formatToVND(number) {
    if (typeof number !== 'number') {
        number = parseFloat(number);
    }
    if (isNaN(number)) return '';

    return number.toLocaleString('vi-VN') + ' VND';
}
function dinhDangNgay(datetimeStr) {
    const date = new Date(datetimeStr);

    const pad = (num) => num.toString().padStart(2, '0');

    const gio = pad(date.getHours());
    const phut = pad(date.getMinutes());
    const giay = pad(date.getSeconds());

    const ngay = pad(date.getDate());
    const thang = pad(date.getMonth() + 1); 
    const nam = date.getFullYear();

    return `${gio}:${phut}:${giay}, ${ngay}/${thang}/${nam}`;
}

function formatDateToString(date) {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = ('0' + (d.getMonth() + 1)).slice(-2);
  const day = ('0' + d.getDate()).slice(-2);
  const hours = ('0' + d.getHours()).slice(-2);
  const minutes = ('0' + d.getMinutes()).slice(-2);
  const seconds = ('0' + d.getSeconds()).slice(-2);

  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
}
export {formatToVND,dinhDangNgay,formatDateToString}