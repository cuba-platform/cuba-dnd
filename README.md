### Preface
The add-on provides Drag-and-Drop functionality for UI platform components [CUBA platform](https://www.cuba-platform.com).

### Dependencies
 - [Vaadin add-on dragdroplayouts](https://github.com/parttio/dragdroplayouts)
 - [Vaadin DragAndDropWrapper](https://vaadin.com/vaadin-documentation-portlet/framework/advanced/advanced-dragndrop.html)

### Main features
UI components with Drag-and-Drop functionality:
  - DDVerticalLayout;
  - DDHorizontalLayout;
  - DDGridLayout;
  - DDCssLayout;
  - DDAbsoluteLayout;
  - DragAndDropWrapper.

### Usage
Select a version of the add-on which is compatible with the platform version used in your project:

| Platform Version| Add-on Version|
|:----------------|:--------------|
| 6.9.x           | 1.3.0         |
| 6.8.x           | 1.2.0         |
| 6.7.x           | 1.1.0         |
| 6.6.x           | 1.0.0         |

Add custom application component to your project (change the version part if needed):
```
com.haulmont.addon.dnd:cuba-dnd-global:1.3.0
```


### Description
This add-on contains components that implement Drag-and-Drop functionality. To handle a component's drop action, it is necessary to implement DropHandler interface that contains two methods:
- `drop(DragAndDropEvent event)` - method that handles transferable component's drop;
- `getCriterion()` - returns the AcceptCriterion that is used to evaluate whether the dragged component will be handed over to drop method.

In order to accept a Drop event, components must provide the `AcceptCriterion` object. `AcceptCriterion` interface has two main inheritors - `ServerSideCriterion` and `AcceptCriterionWrapper`. If you want to check an accept criterion on a browser side, you have to implement `AcceptCriterionWrapper` interface.

### Quick start
To use this add-on in a CUBA Studio project, follow the steps below:

1. In the project properties add add-on like custom application component and save settings.
```
`com.haulmont.addon.dnd:cuba-dnd-global:1.2.0`
```
2. Create a simple screen.
2. Add the following namespace in the XML descriptor of the screen:

```
xmlns:dnd="http://schemas.haulmont.com/dnd/0.1/drag-and-drop.xsd"
```

#### Sample task
Let's try to create a small todo-list app. It will provide a predefined set of todo-actions that can be added to the todo-list.

In this app we will implement the following features:
 - dragging components to the list;
 - reordering components in the list;
 - deleting components.

#### Step-by-step guide
Create a new CUBA project and add the given add-on to it.

![](/sreenshots/1-Adding-addon.png "Adding add-on")

In the GENERIC UI tab create a new screen using "Blank screen" template.

We will divide this screen into two panels: the first panel is a palette that contains actions, the second is a dashboard that contains the list of actions.
First of all, add the next namespace to the XML descriptor of the screen:
```
xmlns:dnd="http://schemas.haulmont.com/dnd/0.1/drag-and-drop.xsd"
```

and add the following block:
```xml
<layout>
    <hbox id="root"
          height="100%"
          width="100%"
          spacing="true"
          expand="rootSpacer">
        <groupBox id="rootPalette"
                  caption="Todo actions"
                  height="100%"
                  width="200px">
            <dnd:dndVBoxLayout id="palette"
                               dragMode="CLONE"
                               height="AUTO"
                               spacing="true"
                               width="100%">
            </dnd:dndVBoxLayout>
        </groupBox>
        <groupBox id="rootDashboard"
                  caption="Todo today"
                  width="500px"
                  height="100%">
            <scrollBox width="100%" height="100%">
                <dnd:dndVBoxLayout id="dashboard"
                                   dragMode="CLONE"
                                   height="AUTO"
                                   spacing="true"
                                   width="100%">
                </dnd:dndVBoxLayout>
            </scrollBox>
        </groupBox>
        <label id="rootSpacer"/>
    </hbox>
</layout>
```
Component with the "rootPalette" id contains the set of predefined actions.
These components can be dragged by users. Component with the "dashboard" id contains a todo-list.

Palette and Dashboard have property `dragMode` which indicates that their components can be dragged.  

This is how the screen looks in the running app:

![](/sreenshots/2-xml-without-buttons.png "View of the screen in the running app")

Next, we should add actions that can be dragged by users and added in their list.
We will use buttons as the components for drag and drop.
Add a set of buttons to the palette with follow ids: call, chat, meeting, buy.

Your XML descriptor should look like this:

```xml
<dnd:dndVBoxLayout id="palette"
                   dragMode="CLONE"
                   height="AUTO"
                   spacing="true"
                   width="100%">
      <button id="call"
              caption="Call"
              height="40px"
              width="100%"/>
      <button id="chat"
              caption="Chat"
              height="40px"
              width="100%"/>
      <button id="meeting"
              caption="Meeting"
              height="40px"
              width="100%"/>
      <button id="buy"
              caption="Buy"
              height="40px"
              width="100%"/>
</dnd:dndVBoxLayout>
```

Make sure that the buttons are added and they can be dragged:

![](/sreenshots/3-xml-with-buttons.png "View of the screen with draggable buttons")

In the screen controller set `DropHandler` to the dashboard component:

```Java
@Inject
private DDVerticalLayout dashboard;

@Override
public void init(Map<String, Object> params) {
    dashboard.setDropHandler(new DropHandler() {
       @Override
       public void drop(DragAndDropEvent event) {

       }

       @Override
       public AcceptCriterion getCriterion() {
           return AcceptCriterion.ACCEPT_ALL;
       }
   });
}
```

The methods `drop()` and `getCriterion()` are invoked every time when the dragged component is dropped to the layout.
In the `drop()` method we should implement our logic to handle component's drop.  

Method `getCriterion()` allows us to specify the criteria to accept dropping various components to this layout
(e.g. only Labels and/or Buttons etc.). In this case we accept all components: `AcceptCriterion.ACCEPT_ALL`.

If we now run this app and try to drag a button to the dashboard, we will see that the dropping area is not highlighted.
This is due to the dashboard property height which is set to AUTO. To expand this layout we should write some CSS in the extension theme of the app.
To do this, we create a theme extension in Project Properties.

![](/sreenshots/4-theme-extention.png "Create extension theme")  

Add the following code to the `halo-ext.scss`:

```css
.min-height{
    min-height:50px;
}
```

and in the XML descriptor of the screen add `stylename="min-height"` to the dashboard component. Launch the app and check:

![](/sreenshots/5-drop-area.png "Drop area")  

Now we should create some panel to display the added action in the list. For this purpose, create the method that returns Component.

```Java
public Component createDashboardElement(Component component) {

}
```

For the main component added directly to the dashboard we will use `GroupBoxLayout`. Then we will add `HBoxLayout` to the `GroupBoxLayout` and fill it with the components:
- Label with appropriate serial number;
- Label with name of action;
- LookupField;
- Button to remove from list.

Firstly, create `GroupBoxLayout` and `HBoxLayout`, specify width 100% for both components and add spacing to `HBoxLayout`:

```Java
GroupBoxLayout groupBox = factory.createComponent(GroupBoxLayout.class);
groupBox.setWidth("100%");

HBoxLayout layout = factory.createComponent(HBoxLayout.class);
layout.setWidth("100%");
layout.setSpacing(true);
```

Next, create main components for this panel. No need to set LookupField width as it will be expanded in HBoxLayout:

```Java
Label countLabel = factory.createComponent(Label.class);
countLabel.setId("countLabel");
countLabel.setWidth("30px");

Label titleLabel = factory.createComponent(Label.class);
titleLabel.setValue(((Button) component).getCaption());
titleLabel.setWidth("60px");

LookupField lookupField = factory.createComponent(LookupField.class);

Button deleteButton = factory.createComponent(Button.class);
deleteButton.setIcon("font-icon:TIMES");
```

Create `BaseAction` for the delete button. We'll implement the logic of deletion a bit later:

```Java
BaseAction action = new BaseAction("remove") {
    @Override
    public void actionPerform(Component component) {
    }
};
action.setCaption("");
deleteButton.setAction(action);

```

It remains to add the created components:

```Java
layout.add(countLabel);
layout.add(titleLabel);
layout.add(lookupField);
layout.expand(lookupField);
layout.add(deleteButton);
groupBox.add(layout);
```

Now we should handle component's drop to the dashboard. `DragAndDropEvent` event variable contains information about which component is being dragged, its source layout, to which component, to which position etc. To get this information you should use the following classes:
- LayoutBoundTransferable;
- DDVerticalLayoutTargetDetails.

In the `drop()` method we get references to the objects `LayoutBoundTransferable` and `DDVerticalLayoutTargetDetails`.

```java
LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();
DDVerticalLayoutTargetDetails details = (DDVerticalLayoutTargetDetails) event.getTargetDetails();
```

`LayoutBoundTransferable` contains two main methods:
- getSourceComponent() - return the layout from which transferable component was dragged;
- getTransferableComponent() - return transferable component;

```Java
Component sourceLayout = t.getSourceComponent();
Component tComponent = t.getTransferableComponent();
```

`DDVerticalLayoutTargetDetails` contains information about layout to drop the component to (receiving layout).
The main methods are:
- `getTarget()` - return layout to which drop;
- `getOverComponent()` - return child component of the target layout, to which drop transferable component;
- `getDropLocation()` - return drop location (Middle, Top, Bottom);
- `getOverIndex()` - return index of child component;
Get references to the layout to drop the transferable component to and `VerticalDropLocation`:

```Java
DDVerticalLayout targetLayout = (DDVerticalLayout) details.getTarget();
VerticalDropLocation loc = details.getDropLocation();
```

Next we should check whether transferable component is null. It is possible if we drag  highlighted html5 text, as it is not a component.

```Java
if (tComponent == null) {
    return;
}
```

Define the index of component to which we drop (indexTo) and the index of transferable component in the target layout (indexFrom). The last index is necessary if the component is replaced in its layout. If a component is dragged from another layout, indexFrom will be -1.

```Java
int indexTo = details.getOverIndex();
int indexFrom = targetLayout.indexOf(tComponent);
```

Create reference to the object that will be added to the layout and check whether component is dragged from another layout, or it is just a reordering within the source layout;

```Java
Component componentToAdd;
if (sourceLayout == targetLayout) {

} else {

}
```

If this condition is fulfilled, we should assign the componentToAdd reference to the dragged component. Then we should check whether the component has changed its position or not. If the component has not changed its location, then finish method.

```Java
componentToAdd = tComponent;
if (indexFrom == indexTo) {
    return;
}
```

If the component has changed its location, we must delete it from its layout. Then check if the index of the receiving component is bigger than the index of the previous location, then we need to reduce it by one. This is because of removing the component from layout.

```Java
targetLayout.remove(tComponent);
if (indexTo > indexFrom) {
    indexTo--;
}
```

If the dragged component does not hit any position, indexTo will be -1. Since the component has already been removed from the layout, it must be added to its previous position.

```Java
if (indexTo == -1) {
    targetLayout.add(componentToAdd, indexFrom);
}
```

In case when component is dragged from another layout, it is necessary to create an appropriate component to display it in the dashboard. If the dragged component does not hit any of the components in the layout, we need to add it to the end of the list of layout components.

```Java
if (sourceLayout == targetLayout) {
    // some code
} else {
    componentToAdd = createDashboardElement(tComponent);
    if (indexTo == -1) {
        targetLayout.add(componentToAdd, targetLayout.getOwnComponents().size());
    }
}
```

Next we should check if the component hits to the child component of layout. If it hits, we check the drop location.
If location is `MIDDLE` or `BOTTOM`, we must increase indexTo by one to add below a component to which the component was dragged.

```java
if (indexTo != -1) {
    if (loc == VerticalDropLocation.MIDDLE || loc == VerticalDropLocation.BOTTOM) {
        indexTo++;
    }
    targetLayout.add(componentToAdd, indexTo);
}
```

As a result, the drop method looks like:

```Java
public void drop(DragAndDropEvent event) {
    LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();
    DDVerticalLayoutTargetDetails details = (DDVerticalLayoutTargetDetails) event.getTargetDetails();

    Component sourceLayout = t.getSourceComponent();
    DDVerticalLayout targetLayout = (DDVerticalLayout) details.getTarget();
    Component tComponent = t.getTransferableComponent();

    VerticalDropLocation loc = details.getDropLocation();

    int indexTo = details.getOverIndex();
    int indexFrom = targetLayout.indexOf(tComponent);

    if (tComponent == null) {
        return;
    }
    Component componentToAdd;

    if (sourceLayout == targetLayout) {
        componentToAdd = tComponent;
        if (indexFrom == indexTo) {
            return;
        }
        targetLayout.remove(tComponent);
        if (indexTo > indexFrom) {
            indexTo--;
        }
        if (indexTo == -1) {
            targetLayout.add(tComponent, indexFrom);
        }
    } else {
        componentToAdd = createDashboardElement(tComponent);
        if (indexTo == -1) {
            targetLayout.add(componentToAdd, targetLayout.getOwnComponents().size());
        }
    }

    if (indexTo != -1) {
        if (loc == VerticalDropLocation.MIDDLE || loc == VerticalDropLocation.BOTTOM) {
            indexTo++;
        }
        targetLayout.add(componentToAdd, indexTo);
    }
}
```

Launch the app and check:

![](/sreenshots/6-check-adding.png "Check adding components")  

Note that serial numbers are not assigned to dashboard components. Create the method to fix it:

```Java
public void updateDashboardComponents(DDVerticalLayout layout) {

}
```
We need to set values to all countLabel. To achieve this, it is necessary to get the list of child dashboard components.

```Java
List<Component> components = new ArrayList<>(layout.getOwnComponents());
int count = 0;
for (Component component : components) {
     GroupBoxLayout groupBox = (GroupBoxLayout) component;
     HBoxLayout hBoxLayout = (HBoxLayout) Iterables.get(groupBox.getComponents(), 0);
     Label label = (Label)  Iterables.get(hBoxLayout.getComponents(), 0);
     label.setValue(++count);
}
```

Now at the end of `drop()` method add the follow invocation:

```Java
updateDashboardComponents(targetLayout);
```

It remains to implement the component removing from the dashboard. Add this code to `actionPerfom()` method:

```Java
public void actionPerform(Component component) {
    HBoxLayout hBox = (HBoxLayout) component.getParent();
    GroupBoxLayout groupBox = (GroupBoxLayout) hBox.getParent();
    DDVerticalLayout ddLayout = (DDVerticalLayout) groupBox.getParent();
    ddLayout.remove(groupBox);
    updateDashboardComponents(ddLayout);
}
```

Launch the app and check that the serial numbers are updated and the components are removed.
