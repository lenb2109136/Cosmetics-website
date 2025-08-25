export class Classify {
  constructor(ten, id) {
    this.classifyItems = [];
    this.ten = ten;
    this.id = id;
  }

  removeItemByName(name) {
    if (this.classifyItems.length === 0) return 0;
    this.classifyItems = this.classifyItems.filter(item => item.ten !== name);
    return this.classifyItems.length;
  }

  removeItemById(id) {
    if (this.classifyItems.length === 0) return 0;
    this.classifyItems = this.classifyItems.filter(item => item.id !== id);
    return this.classifyItems.length;
  }
  testEmpty() {
    for (let i = 0; i < this.classifyItems?.length; i++) {
      if (this.classifyItems[i].ten === "") {
        return i;
      }
    }
    return -1;
  }
}



export class ClassifyItem {
  constructor(id, ten) {
    this.id = id;
    this.ten = ten;
  }
}

export class Variant {
  constructor(combination = []) {
    this.combination = combination;
    this.price = 0;
  }



  addCombination(classifyItem) {
    const exists = this.combination.some(item => item.ten === classifyItem.ten);
    if (exists) return false;
    this.combination.push(classifyItem);
    return true;
  }

  removeCombinationName(name) {
    if (this.combination.length === 0) return 0;
    this.combination = this.combination.filter(item => item.ten !== name);
    return this.combination.length;
  }

  removeCombinationId(id) {
    if (this.combination.length === 0) return 0;
    this.combination = this.combination.filter(item => item.id !== id);
    return this.combination.length;
  }


}


export class Product {
  constructor() {
    this.bienTheKhongLe=[];
    this.ten = "";
    this.thuongHieu = 0;
    this.moTa = "";
    this.thanhPhan = ""
    this.cachDung = ""
    this.danhMuc = 0;
    this.thongSo = [];
    this.thue=0;
    this.gia = 0;
    this.dongGoiNhap=[]
  }
  changeTen(classifyitem, ten) {
    let pr = new Product();

    pr.variant = structuredClone(this.variant);
    pr.classify = structuredClone(this.classify);

    for (let i = 0; i < pr.variant.length; i++) {
      for (let u = 0; u < pr.variant[i].combination.length; u++) {
        if (pr.variant[i].combination[u].id === classifyitem.id) {
          classifyitem.ten = ten;
          pr.variant[i].combination[u] = classifyitem;
          break;
        }
      }
    }

    for (let i = 0; i < pr.classify.length; i++) {
      for (let u = 0; u < pr.classify[i].classifyItems.length; u++) {
        if (pr.classify[i].classifyItems[u].id === classifyitem.id) {
          classifyitem.ten = ten;
          pr.classify[i].classifyItems[u] = classifyitem;
          break;
        }
      }
    }

    return pr;
  }




}
function CheckClassify(id, classify) {
  for (let i = 0; i < classify?.length; i++) {
    if (classify[i].id == id) {
      return true;
    }
  }
  return false;
}
function removeDuplicatesById(m1, m2) {
  console.log("m1")
  console.log(m1)
  console.log("m2")
  console.log(m2)
  if (!Array.isArray(m1) || m1.length === 0) return [];
  if (!Array.isArray(m2) || m2.length === 0) return m1.map(item => new ClassifyItem(item.id, item.ten));

  const idsToRemove = new Set(m2.map(item => item.id));
  return m1
    .filter(item => !idsToRemove.has(item.id))
    .map(item => new ClassifyItem(item.id, item.ten));
}
function removeClassifyById(product, classifyId, removedIndexesRef) {
  const productCopy = Object.assign(
    Object.create(Object.getPrototypeOf(product)),
    JSON.parse(JSON.stringify(product))
  );

  const classifyToRemove = productCopy.classify.find(cls => cls.id === classifyId);
  if (!classifyToRemove) return productCopy;

  const idsToRemove = new Set(classifyToRemove.classifyItems.map(item => item.id));

  // Ghi nhớ index các variant bị xóa
  const filteredVariants = [];
  productCopy.variant.forEach((variant, index) => {
    const newCombination = variant.combination.filter(item => !idsToRemove.has(item.id));
    if (newCombination.length <2) {
      removedIndexesRef.current.push(index);
    } else {
      filteredVariants.push({
        ...variant,
        combination: newCombination
      });
    }
  });

  productCopy.variant = filteredVariants;

  productCopy.classify = productCopy.classify.filter(cls => cls.id !== classifyId);

  return productCopy;
}

function changeClassifyTenByIndex(classifyIndex, newTen, pr) {
  const newProduct = new Product();
  newProduct.variant = structuredClone(pr.variant);
  newProduct.classify = structuredClone(pr.classify);

  if (
    classifyIndex >= 0 &&
    classifyIndex < newProduct.classify.length &&
    typeof newTen === "string"
  ) {
    newProduct.classify[classifyIndex].ten = newTen;
  }

  return newProduct;
}

function checkSubmit(p, anhbia, anhgioithieu, anhbienthe) {
  if (anhbia == null) {
    return {
      test: false,
      message: "Vui lòng chọn cung cấp ảnh bìa cho sản phẩm"
    }
  }
  if (anhgioithieu?.length < 2) {
    return {
      test: false,
      message: "Vui lòng chọn nhiều hơn 2 ảnh giới thiệu"
    }
  }
  if (p?.bienTheKhongLe?.length != anhbienthe?.length) {
    alert(p?.bienTheKhongLe?.length)
    alert(anhbienthe?.length)
    return {
      test: false,
      message: "Vui lòng cung cấp đầy đủ ảnh biến thể"
    }
  }
  if (p.gia == 0 && p?.bienTheKhongLe?.length == 0) {
    return {
      test: false,
      message: "Vui lòng cung cấp giá mặc định"
    }
  }
  if (p.danhMuc == 0) {
    return {
      test: false,
      message: "Vui lòng chọn danh mục cho sản phẩm"
    }
  }
  if (p.thuongHieu == 0) {
    return {
      test: false,
      message: "Vui lòng chọn thương hiệu cho sản phẩm"
    }
  }
  if (p.ten == "") {
    return {
      test: false,
      message: "Tên sản phẩm không được để trống"
    }
  }
  if (p.moTa == "") {
    return {
      test: false,
      message: "Mô tả sản phẩm không được để trống"
    }
  }
  if (p.thanhPhan == "") {
    return {
      test: false,
      message: "thành phần sản phẩm không được để trống"
    }
  }
  if (p.cachDung == "") {
    return {
      test: false,
      message: "Cách dùng  sản phẩm không được để trống"
    }
  }
  let m;
  p?.bienTheKhongLe.forEach(data => {
  if (data.price <= 0) {
     m= {
        test: false,
        message: "Giá của mỗi phân loại không được nhỏ hơn 0"
      }
    }
  })
  if(m!=null){
    return m;
  }
  return {
    test: true
  }
}



export { CheckClassify, removeDuplicatesById, removeClassifyById, changeClassifyTenByIndex, checkSubmit }


