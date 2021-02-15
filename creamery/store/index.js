export const state = () => ({
  shiftStatus: null,
  shiftTime: null,
})

export const mutations = {
  setShiftStatus(state, shiftStatus) {
    state.shiftStatus = shiftStatus
  },
  setShiftTime(state, shiftTime) {
    state.shiftTime = shiftTime
  },
}
