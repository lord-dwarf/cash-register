export const state = () => ({
  authJwt: null,
  userRole: null,
  userName: null,
  userLocale: 'ua',
  errorMessage: null,
  productsOne: {
    visible: false,
    initialized: false,
    mode: null,
    product: null,
  },
  receiptsOne: {
    visible: false,
    mode: null,
    receiptId: null,
  },
  myReceiptsOne: {
    visible: false,
    mode: null,
    receiptId: null,
  },
})

export const mutations = {
  // Auth state
  setAuthJwt(state, authJwt) {
    state.authJwt = authJwt
  },
  setUserRole(state, userRole) {
    state.userRole = userRole
  },
  setUserName(state, userName) {
    state.userName = userName
  },
  // Error state
  setErrorMessage(state, errorMessage) {
    state.errorMessage = errorMessage
  },
  // ProductsOne page state
  viewProductsOne(state, product) {
    state.productsOne.visible = true
    state.productsOne.initialized = false
    state.productsOne.mode = 'VIEW'
    state.productsOne.product = product
  },
  editProductsOne(state, product) {
    state.productsOne.visible = true
    state.productsOne.initialized = false
    state.productsOne.mode = 'EDIT'
    state.productsOne.product = product
  },
  addProductsOne(state) {
    state.productsOne.visible = true
    state.productsOne.initialized = false
    state.productsOne.mode = 'ADD'
    state.productsOne.product = {
      id: null,
      code: null,
      category: null,
      name: null,
      details: null,
      price: 0.0,
      amountAvailable: 0.0,
      amountUnit: 'KILO',
    }
  },
  closeProductsOne(state) {
    state.productsOne.visible = false
    state.productsOne.initialized = false
    state.productsOne.mode = null
    state.productsOne.product = {}
  },
  setProductsOneInitialized(state) {
    state.productsOne.initialized = true
  },
  setProductsOneCode(state, productCode) {
    state.productsOne.product.code = productCode
  },
  setProductsOneCategory(state, productCategory) {
    state.productsOne.product.category = productCategory
  },
  setProductsOneName(state, productName) {
    state.productsOne.product.name = productName
  },
  setProductsOneDetails(state, productDetails) {
    state.productsOne.product.details = productDetails
  },
  setProductsOnePrice(state, productPrice) {
    state.productsOne.product.price = productPrice
  },
  setProductsOneAmountAvailable(state, productAmountAvailable) {
    state.productsOne.product.amountAvailable = productAmountAvailable
  },
  setProductsOneAmountUnit(state, productAmountUnit) {
    state.productsOne.product.amountUnit = productAmountUnit
  },

  // ReceiptsOne page state
  viewReceiptsOne(state, receiptId) {
    state.receiptsOne.visible = true
    state.receiptsOne.mode = 'VIEW'
    state.receiptsOne.receiptId = receiptId
  },
  editReceiptsOne(state, receiptId) {
    state.receiptsOne.visible = true
    state.receiptsOne.mode = 'EDIT'
    state.receiptsOne.receiptId = receiptId
  },
  closeReceiptsOne(state) {
    state.receiptsOne.visible = false
    state.receiptsOne.mode = null
    state.receiptsOne.receiptId = null
  },

  // MyReceiptsOne page state
  viewMyReceiptsOne(state, receiptId) {
    state.myReceiptsOne.visible = true
    state.myReceiptsOne.mode = 'VIEW'
    state.myReceiptsOne.receiptId = receiptId
  },
  editMyReceiptsOne(state, receiptId) {
    state.myReceiptsOne.visible = true
    state.myReceiptsOne.mode = 'EDIT'
    state.myReceiptsOne.receiptId = receiptId
  },
  newMyReceiptsOne(state) {
    state.myReceiptsOne.visible = true
    state.myReceiptsOne.mode = 'NEW'
    state.myReceiptsOne.receiptId = null
  },
  closeMyReceiptsOne(state) {
    state.myReceiptsOne.visible = false
    state.myReceiptsOne.mode = null
    state.myReceiptsOne.receiptId = null
  },
  setMyReceiptsOneReceiptId(state, receiptId) {
    state.myReceiptsOne.receiptId = receiptId
  },
}
