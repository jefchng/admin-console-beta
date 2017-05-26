import React from 'react'
import { connect } from 'react-redux'
import { withApollo } from 'react-apollo'

import { getAllConfig } from 'admin-wizard/reducer'
import { errorCode, friendlyMessage } from 'graphql-errors'

import { getSourceName, getChosenEndpoint, getDiscoveredEndpoints, getErrors } from '../reducer'
import { changeStage, startSubmitting, endSubmitting, setErrors, clearErrors  } from '../actions'
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

const currentStageId = 'confirmationStage'

const ConfirmationStageView = ({ messages, sourceName, client, inputConfigs, config, type, changeStage, startSubmitting, endSubmitting, setErrors, clearErrors }) => {

  return (
    <NavPanes backClickTarget='sourceSelectionStage' forwardClickTarget='completedStage'>
      <Title>
        Finalize Source Configuration
      </Title>
      <Description>
        Please give your source a unique name, confirm details, and press finish to create source.
      </Description>
      <div style={{ width: 400, position: 'relative', margin: '0px auto', padding: 0 }}>
        <Input id='sourceName' label='Source Name' autoFocus />
        <Info label='Source Address' value={config.endpointUrl} />
        <Info label='Username' value={inputConfigs.sourceUserName || 'none'} />
        <Info label='Password' value={inputConfigs.sourceUserPassword ? '*****' : 'none'} />
        {messages.map((msg, i) => <Message key={i} message={msg} type='FAILURE' />)}
      </div>
      <ActionGroup>
        <Action
          primary
          label='Finish'
          disabled={sourceName === undefined || sourceName.trim() === ''}
          onClick={() => {
            startSubmitting()
            client.mutate(saveSource({
              type,
              config,
              sourceName,
              creds: {
                username: inputConfigs.sourceUserName,
                password: inputConfigs.sourceUserPassword
              }}))
              .then(
                ({ data }) => {
                  endSubmitting()
                  if(data.errors){
                    setErrors(currentStageId, data.errors.map(({ code }) => friendlyMessage[code]))
                  } else {
                    clearErrors()
                    changeStage('completedStage')
                  }
                })
              .catch(() => {
                endSubmitting()
                setErrors(currentStageId, ['Network Error'])
            })
          }} />
      </ActionGroup>
    </NavPanes>
  )
}

let ConfirmationStage = connect((state) => ({
  sourceName: getSourceName(state),
  messages: getErrors(state)(currentStageId),
  inputConfigs: getAllConfig(state),
  type: getChosenEndpoint(state),
  config: getDiscoveredEndpoints(state)[getChosenEndpoint(state)]
}), ({
  changeStage,
  startSubmitting,
  endSubmitting,
  setErrors,
  clearErrors
}))(ConfirmationStageView)

export default withApollo(ConfirmationStage)