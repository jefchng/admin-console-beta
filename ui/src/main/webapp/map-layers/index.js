import React from "react";
import {List, ListItem} from "material-ui/List";
import AddIcon from "material-ui/svg-icons/content/add";
import Flexbox from "flexbox-react";
import {Input, Select, InputAuto} from "admin-wizard/inputs";
import Divider from "material-ui/Divider";
import Slider from "material-ui/Slider";
import Paper from "material-ui/Paper";
import FlatButton from "material-ui/FlatButton";
import RaisedButton from "material-ui/RaisedButton";
import EditableList from "./editable-list";

const LayerEditor = () => (
    <Paper style={{padding: 10}} zDepth={1}>
      <Input id='layerName' label='Name'/>
        <div>Alpha</div>
        <Slider
          min={0}
          max={100}
          step={.01}
          style={{display: 'inline'}}
        />
      {/*<InputAuto type='number' value={value} label={label} {...rest} />*/}
      <div>Parameter Override</div>
      <Flexbox justifyContent='center'>
        <FlatButton secondary label='cancel' style={{marginRight: 10}}/>
        <RaisedButton primary label='add'/>
      </Flexbox>
    </Paper>
)

const LayerInfo = () => (
  <List>
    <h3 style={{textAlign: 'center', margin: 0}}>Layers</h3>
    <ListItem
      primaryText="Create Map Layer"
      leftIcon={<AddIcon />}
      initiallyOpen={true}
      primaryTogglesNestedList={true}
      nestedItems={[<LayerEditor key='layer-editor' />]} />
    <ListItem
      key={2}
      primaryText="Layer #1"
      disabled={true}
      nestedItems={[<LayerEditor key='layer-editor' />]}
    />
    <ListItem
      key={3}
      primaryText="Layer #2"
      disabled={true}
      nestedItems={[<LayerEditor key='layer-editor' />]}
    />
    <ListItem
      key={4}
      primaryText="Layer #3"
      disabled={true}
      nestedItems={[<LayerEditor key='layer-editor' />]}
    />
    <ListItem
      key={5}
      primaryText="Layer #4"
      disabled={true}
      nestedItems={[<LayerEditor key='layer-editor' />]}
    />
  </List>
)

const ProviderInfo = () => (
  <div>
    <h3 style={{textAlign: 'center', margin: 0}}>Provider Info</h3>
    <div style={{margin: '0 25px'}}>
      <Input
        id='providerUrl'
        label='Provider URL'/>
      <Select
        id='providerType'
        label='Provider Layer Type'
        options={['A', 'B', 'C']}
      />
      <EditableList
        /*hintText='Add New Path'*/
        list={['testing', 'testing2']}
        /*errors={errors}*/
        /*onChange={({ value, index }) => onUpdate(value === '' ? undefined : value, index) />*/
        />
    </div>
  </div>
)

const ProviderEditor = () => (
  <div style={{ minWidth: '100%' }}>
    <ProviderInfo />
    <Divider style={{ margin: '20px 0' }} />
    <LayerInfo />
  </div>
)

const Providers = () => (
  <List style={{ borderRight: '1px solid rgb(224, 224, 224)', padding: 0 }}>
    <ListItem
      primaryText="Add Provider"
      leftIcon={<AddIcon />}
      initiallyOpen={true}
      primaryTogglesNestedList={true}
    />
    <Divider />
    <ListItem
      primaryText="Provider #1"
      initiallyOpen={true}
      primaryTogglesNestedList={true}
    />
    <Divider />
    <ListItem
      primaryText="Provider #2"
      initiallyOpen={true}
      primaryTogglesNestedList={true}
    />
    <Divider />
    <ListItem
      primaryText="Provider #3"
      initiallyOpen={true}
      primaryTogglesNestedList={true}
    />
    <Divider />
    <ListItem
      primaryText="Provider #4"
      initiallyOpen={true}
      primaryTogglesNestedList={true}
    />
    <Divider />
    <ListItem
      primaryText="Provider #5"
      initiallyOpen={true}
      primaryTogglesNestedList={true}
    />
  </List>
)

const MapLayers = () => (
  <div>
    <h1>Map Layers Configuration</h1>
    <Paper>
      <Flexbox style={{ marginTop: 20 }}>
        <Flexbox>
          <Providers />
        </Flexbox>
        <Flexbox flex="1" style={{ margin: 20 }}>
          <ProviderEditor />
        </Flexbox>
      </Flexbox>
    </Paper>
  </div>
)

export default MapLayers