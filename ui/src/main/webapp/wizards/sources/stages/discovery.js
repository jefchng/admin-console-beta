import React from 'react'
import { connect } from 'react-redux'
import Mount from 'react-mount'
import { withApollo } from 'react-apollo'
import { getDiscoveryType } from '../reducer'
import { setDiscoveryType, changeStage, setDiscoveredEndpoints, startSubmitting, endSubmitting } from '../actions'
import { NavPanes, SideLines } from '../components'
import { setDefaults } from '../../../actions'
import { discoverSources, filterAvailableEndpoints } from '../graphql-queries/source-discovery'
import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'
import {getAllConfig, getMessages} from 'admin-wizard/reducer'
import {Input, Password, Hostname, Port} from 'admin-wizard/inputs'
import FlatButton from 'material-ui/FlatButton'

const isEmpty = (string) => {
  return !string
}

const isBlank = (string) => {
  return !string || !string.trim()
}

const discoveryStageDefaults = {
  sourceHostName: '',
  sourcePort: 8993
}

const DiscoveryStageView = ({ messages, setDefaults, configs, discoveryType, setDiscoveryType, changeStage, client, setDiscoveredEndpoints, startSubmitting, endSubmitting }) => {
  const nextShouldBeDisabled = () => {
    // checks that username & password are either both filled out or both empty (because it's optional)
    if (isBlank(configs.sourceUserName) !== isEmpty(configs.sourceUserPassword)) {
      return true
    }

    // hostname & port discovery checks
    if (discoveryType === 'hostnamePort') {
      if (isBlank(configs.sourceHostName) || configs.sourcePort < 0) {
        return true
      }
    }

    // url discovery checks
    if (discoveryType === 'url') {
      if (isBlank(configs.endpointUrl)) {
        return true
      }
    }

    return false
  }

  return (
    <Mount on={() => setDefaults(discoveryStageDefaults)}>
      <NavPanes backClickTarget='welcomeStage' forwardClickTarget='sourceSelectionStage'>
        <Title>
          Discover Available Sources
        </Title>
        <Description>
          Enter connection information to scan for available sources on a host.
        </Description>
        <div style={{ width: 400, position: 'relative', margin: '0px auto', padding: 0 }}>

          <Hostname
            visible={discoveryType === 'hostnamePort'}
            id='sourceHostName'
            label='Host'
            autoFocus />
          <Port
            visible={discoveryType === 'hostnamePort'}
            id='sourcePort'
            label='Port'
            errorText={(configs.sourcePort < 0) ? 'Port is not in valid range.' : undefined} />
          <Input
            visible={discoveryType === 'url'}
            id='endpointUrl'
            label='Source URL'
            autoFocus />
          {
            (discoveryType === 'hostnamePort') ? (
              <div style={{ textAlign: 'right' }}>
                <FlatButton primary labelStyle={{fontSize: '14px', textTransform: 'none'}} label='Know the source url?' onClick={() => { setDiscoveryType('url') }} />
              </div>
            ) : (
              <div style={{ textAlign: 'right' }}>
                <FlatButton primary labelStyle={{fontSize: '14px', textTransform: 'none'}} label={'Don\'t know the source url?'} onClick={() => { setDiscoveryType('hostnamePort') }} />
              </div>
            )
          }
          <SideLines label='Authentication (Optional)' />
          <Input
            id='sourceUserName'
            label='Username'
            errorText={(isBlank(configs.sourceUserName) && !isEmpty(configs.sourceUserPassword)) ? 'Password with no username.' : undefined} />
          <Password
            id='sourceUserPassword'
            label='Password'
            errorText={(!isBlank(configs.sourceUserName) && isEmpty(configs.sourceUserPassword)) ? 'Username with no password.' : undefined} />
          {messages.map((msg, i) => <Message key={i} {...msg} />)}
          <ActionGroup>
            <Action
              primary
              label='Check'
              disabled={nextShouldBeDisabled()}
              onClick={() => {
                startSubmitting()
                client.query(discoverSources({ configs, discoveryType }))
                  .then(({data}) => {
                    setDiscoveredEndpoints(filterAvailableEndpoints(data))
                    endSubmitting()
                    changeStage('sourceSelectionStage')
                  })
                  .catch((errors) => {
                    endSubmitting()
                    console.log(errors)
                  })
              }
              } />
          </ActionGroup>
        </div>
      </NavPanes>
    </Mount>
  )
}

let DiscoveryStage = connect((state) => ({
  configs: getAllConfig(state),
  messages: getMessages(state, 'discoveryStage'),
  discoveryType: getDiscoveryType(state)
}), {
  setDefaults,
  setDiscoveryType,
  changeStage,
  setDiscoveredEndpoints,
  startSubmitting,
  endSubmitting
})(DiscoveryStageView)

export default withApollo(DiscoveryStage)
