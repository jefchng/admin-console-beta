import { combineReducers } from 'redux-immutable'
import { fromJS, Map } from 'immutable'
import sub from 'redux-submarine'

const sourceStage = (state = 'welcomeStage', { type, stage }) => {
  switch (type) {
    case 'SOURCE_CHANGE_STAGE':
      return stage
    case 'SOURCE_NAV_STAGE':
      return stage
    case 'CLEAR_WIZARD': // also make clear config info
      return 'welcomeStage'
    default:
      return state
  }
}

const sourceStagesClean = (state = false, { type }) => {
  switch (type) {
    case 'SOURCE_CLEAN_STAGES':
      return true
    case 'SOURCE_MODIFIED_STAGES':
      return false
    case 'EDIT_CONFIG':
      return false
    case 'SOURCE_CHANGE_STAGE':
      return true
    case 'CLEAR_WIZARD':
      return false
    default:
      return state
  }
}

const sourceStageProgress = (state = 'welcomeStage', { type, stage }) => {
  switch (type) {
    case 'SET_CURRENT_PROGRESS':
      return stage
    case 'SOURCE_CHANGE_STAGE':
      return stage
    case 'CLEAR_WIZARD':
      return 'welcomeStage'
    default:
      return state
  }
}

const chosenEndpoint = (state = '', { type, endpointKey }) => {
  switch (type) {
    case 'SOURCES/SET_CHOSEN_ENDPOINT':
      return endpointKey
    case 'CLEAR_WIZARD':
      return ''
    default:
      return state
  }
}

const discoveredEndpoints = (state = Map(), { type, endpointConfigs }) => {
  switch (type) {
    case 'SOURCES/SET_DISCOVERED_ENDPOINTS':
      return fromJS(endpointConfigs)
    case 'CLEAR_WIZARD':
      return Map()
    default:
      return state
  }
}

const errors = (state = Map(), { type, stageId, errorList, error }) => {
  switch (type) {
    case 'SOURCES/SET_ERRORS':
      return state.set(stageId, errorList)
    case 'SOURCES/ADD_ERROR':
      return state.set(stageId, state.get(stageId).push(error))
    case 'SOURCES/CLEAR_ERRORS':
    case 'CLEAR_WIZARD':
      return Map()
    default:
      return state

  }
}

const isSubmitting = (state = false, { type }) => {
  switch (type) {
    case 'START_SUBMITTING':
      return true
    case 'END_SUBMITTING':
      return false
    case 'CLEAR_WIZARD':
      return false
    default:
      return state
  }
}

const configTypes = (state = fromJS([]), { type, types }) => {
  switch (type) {
    case 'SOURCES/SET_CONFIG_IDS':
      return fromJS(types)
    default:
      return state
  }
}

const discoveryType = (state = 'hostnamePort', { type, value }) => {
  switch (type) {
    case 'SOURCES/DISCOVERY_TYPE/SET':
      return value
    default:
      return state
  }
}

export const getConfigTypeById = (state, id) => {
  const found = getConfigTypes(state).filter((config) => config.id === id)
  if (found.length > 0) {
    return found[0].name
  }
}

export const getDiscoveryConfigs = (state) => (type) => {
  const sourceUserName = state.getIn(['wizard', 'config', 'sourceUserName', 'value'])
  const sourceUserPassword = state.getIn(['wizard', 'config', 'sourceUserPassword', 'value'])
  switch (type) {
    case 'hostnamePort':
      const sourceHostName = state.getIn(['wizard', 'config', 'sourceHostName', 'value'])
      const sourcePort = state.getIn(['wizard', 'config', 'sourcePort', 'value'])
      return {
        sourceUserName,
        sourceUserPassword,
        sourceHostName,
        sourcePort
      }

    case 'url':
      const endpointUrl = state.getIn(['wizard', 'config', 'endpointUrl', 'value'])
      return {
        sourceUserName,
        sourceUserPassword,
        endpointUrl
      }

    default:
      return {
        sourceUserName,
        sourceUserPassword
      }
  }
}

export const submarine = sub()
export const getIsSubmitting = (state) => submarine(state).get('isSubmitting')
export const getDiscoveryType = (state) => submarine(state).get('discoveryType')
export const getSourceStage = (state) => submarine(state).get('sourceStage')
export const getStageProgress = (state) => submarine(state).get('sourceStageProgress')
export const getStagesClean = (state) => submarine(state).get('sourceStagesClean')
export const getConfigTypes = (state) => submarine(state).get('configTypes').toJS()
export const getErrors = (state) => (stageId) => submarine(state).getIn(['errors', stageId], [])

export const getConfig = (state, id) => state.getIn(['wizard', 'config', id], Map()).toJS()
export const getConfigurationHandlerId = (state) => state.getIn(['wizard', 'config', 'configurationHandlerId'])
export const getSourceName = (state) => state.getIn(['wizard', 'config', 'sourceName', 'value'])

export const getDiscoveredEndpoints = (state) => submarine(state).get('discoveredEndpoints').toJS()
export const getChosenEndpoint = (state) => submarine(state).get('chosenEndpoint')

export default combineReducers({ sourceStage, sourceStagesClean, sourceStageProgress, isSubmitting, configTypes, discoveryType, discoveredEndpoints, chosenEndpoint, errors })

