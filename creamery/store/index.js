export const state = () => ({
  userRole: null,
  productsOne: {
    visible: false,
    navMenuTitle: null,
    product: null,
    readonly: null,
  },
})

export const mutations = {
  setUserRole(state, userRole) {
    state.userRole = userRole
  },
  viewProductsOne(state, product) {
    state.productsOne.visible = true
    state.productsOne.navMenuTitle = 'VIEW Product'
    state.productsOne.product = product
    state.productsOne.readonly = true
  },
  editProductsOne(state, product) {
    state.productsOne.visible = true
    state.productsOne.navMenuTitle = 'EDIT Product'
    state.productsOne.product = product
    state.productsOne.readonly = false
  },
  addProductsOne(state) {
    state.productsOne.visible = true
    state.productsOne.navMenuTitle = 'ADD Product'
    state.productsOne.product = {}
    state.productsOne.readonly = false
  },
}
