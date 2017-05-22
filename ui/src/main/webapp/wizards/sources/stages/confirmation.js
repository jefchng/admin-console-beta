import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import { getSourceName, getChosenEndpoint, getDiscoveredEndpoints } from '../reducer'
import { changeStage, startSubmitting, endSubmitting } from '../actions'
import { getAllConfig, getMessages } from 'admin-wizard/reducer'
import { NavPanes } from '../components.js'
import { saveSource } from '../graphql-mutations/source-persist'

import Info from 'components/Information'
import Title from 'components/Title'
import Description from 'components/Description'
import ActionGroup from 'components/ActionGroup'
import Action from 'components/Action'
import Message from 'components/Message'

import {
  Input
} from 'admin-wizard/inputs'

const ConfirmationStageView = ({ messages, sourceName, client, inputConfigs, config, type, changeStage, startSubmitting, endSubmitting }) => {
  const sourceConfig = config[Object.keys(config)[0]].sourceConfig

  return (<NavPanes backClickTarget='sourceSelectionStage' forwardClickTarget='completedStage'>
    <Title>
      Finalize Source Configuration
    </Title>
    <Description>
      Please give your source a unique name, confirm details, and press finish to create source.
    </Description>
    <div style={{ width: 400, position: 'relative', margin: '0px auto', padding: 0 }}>
      <Input id='sourceName' label='Source Name' autoFocus />
      <Info label='Source Address' value={sourceConfig.endpointUrl} />
      <Info label='Username' value={inputConfigs.sourceUserName || 'none'} />
      <Info label='Password' value={inputConfigs.sourceUserPassword ? '*****' : 'none'} />
      {messages.map((msg, i) => <Message key={i} {...msg} />)}
    </div>
    <ActionGroup>
      <Action
        primary
        label='Finish'
        disabled={sourceName === undefined || sourceName.trim() === ''}
        onClick={() => {
          startSubmitting()
          client.mutate(saveSource({ type, sourceConfig, sourceName, creds: { username: inputConfigs.sourceUserName, password: inputConfigs.sourceUserPassword } }))
          .then(() => {
            endSubmitting()
            changeStage('completedStage')
          })
          .catch((errors) => {
            endSubmitting()
            console.log(errors)
          })
        }} />
    </ActionGroup>
  </NavPanes>
  )
}

let ConfirmationStage = connect((state) => ({
  sourceName: getSourceName(state),
  messages: getMessages(state, 'confirmationStage'),
  inputConfigs: getAllConfig(state),
  type: getChosenEndpoint(state),
  config: getDiscoveredEndpoints(state)[getChosenEndpoint(state)]
}), ({
  changeStage,
  startSubmitting,
  endSubmitting
}))(ConfirmationStageView)

export default withApollo(ConfirmationStage)
