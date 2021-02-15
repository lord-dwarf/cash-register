export const state = () => ({
  shiftStatus: null,
})

export const mutations = {
  setShiftStatus(state, shiftStatus) {
    state.shiftStatus = shiftStatus
  },
}
