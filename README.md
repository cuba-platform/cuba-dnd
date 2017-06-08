### What is it?
This is add-on, that provides Drag & Drop functionality for UI platform components [CUBA.platform](https://www.cuba-platform.com).

### Dependencies
 - [vaadin add-on dragdroplayouts](https://github.com/parttio/dragdroplayouts)
 - [vaadin DragAndDropWrapper](https://vaadin.com/vaadin-documentation-portlet/framework/advanced/advanced-dragndrop.html)

### Main features
UI components with Drag & Drop functionality:
  - DDVerticalLayout;
  - DDHorizontalLayout;
  - DDGridLayout;
  - DDCssLayout;
  - DDAbsoluteLayout;
  - DragAndDropWrapper.

### Build from sources
```
gradlew install
```

### Description
This add-on contains components, that implement Drag & Drop functionality. To handle component's drop action it is necessary to implement DropHandler interface, that contains two methods:
- drop(DragAndDropEvent event) - method that handle transferable component's drop;
- getCriterion() - returns the AcceptCriterion that used to evaluate whether the dragged component will be handed over to drop method.

In order to accept Drop event components must provide AcceptCriterion object. AcceptCriterion interface has two main inheritors - ServerSideCriterion and AcceptCriterionWrapper. If you want to check accept criterion on a browser side you have to implement AcceptCriterionWrapper interface.

### Quick start
To use this add-on in Cuba Studio project follow next steps:

1. Download it.
2. In Cuba Studio Project Properties click on Edit. In the App Components section add this add-on.
2. In xml file of your screen you should add following xmlns:

```
xmlns:dnd="http://schemas.haulmont.com/dnd/0.1/drag-and-drop.xsd"
```

After completing this steps in Cuba Studio project will be available dnd components.

#### Sample task
Let's try to create a small todo-list app. It will provide a predefined set of tod-actions, that can be added to todo-list.

To do this app it is necessary to implement next features:
 - dragging components to the list;
 - reordering components in the list;
 - deleting components.

#### Step-by-step guide
Create a new CUBA project and add this add-on.

![](/sreenshots/1-Adding-addon.png "Adding add-on")

In the GENERIC UI tab create new "Blank screen".

In this app we create two panels. First panel is a palette that contains actions,
second is a dashboard that contains list of actions.
First of all, add dependency in xml of the screen:
```
xmlns:dnd="http://schemas.haulmont.com/dnd/0.1/drag-and-drop.xsd"
```

and add the following XML block:
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
Component with id "rootPalette" contains set of predefined actions.
These components can be dragged by users. Component with id "dashboard" contains a todo-list.

Palette and Dashboard have property dragMode, which indicates that their components can be dragged.  

View of this xml in the running app:

![](/sreenshots/2-xml-without-buttons.png "View of xml in the running app")

Next we should add actions that can be dragged by users and added in their list.
As components for drag and drop we use buttons.
Add set of buttons to the palette with follow ids: call, chat, meeting, buy.

Your xml file should look like this:

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

![](/sreenshots/3-xml-with-buttons.png "View of this xml with dragging buttons")

In the controller of the screen set DropHandler to the dashboard component:

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

Methods drop and getCriterion are invoked every time when dragging component is dropped to layout.
In drop method we should implement our logic to handle component's drop.  

Method getCriterion allows to specify criteria which accept dropping to this layout various components
(e.g. only Labels and/or Buttons etc.). In this case we accept all components: AcceptCriterion.ACCEPT_ALL.

If we run this app and try to drag button to the dashboard we will see that the dropping area is not highlighted.
This is due to dashboard property height is AUTO. To expand this layout we should write some CSS in the extension theme of this app.
To do this, we create extension theme in Project Properties.

![](/sreenshots/4-theme-extention.png "Create extension theme")  

Add follow code to the halo-ext.scss:

```css
.min-height{
    min-height:50px;
}
```

and in the xml file of the screen add `stylename="min-height"` to the dashboard component. Launch and check:

![](/sreenshots/5-drop-area.png "Drop area")  

Now we should create some panel to view added action in the list. For this task create next method that will return Component.

```Java
public Component createDashboardElement(Component component) {

}
```

As main component that added directly to the dashboard we use GroupBoxLayout. Add to the GroupBoxLayout HBoxLayout that contains next components:
- Label with appropriate serial number;
- Label with name of action;
- LookupField;
- Button to remove from list.

First create GroupBoxLayout and HBoxLayout, specify each component width 100% and add spacing to HBoxLayout:

```Java
GroupBoxLayout groupBox = factory.createComponent(GroupBoxLayout.class);
groupBox.setWidth("100%");

HBoxLayout layout = factory.createComponent(HBoxLayout.class);
layout.setWidth("100%");
layout.setSpacing(true);
```

Next create main components for this panel. Don't need to set LookupField width because it will be expanded in HBoxLayout:

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

For delete button create BaseAction. The logic of deletion is implemented a little later:

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

Now we should handle component's drop to the dashboard. DragAndDropEvent event variable contains information about which component is dragged, it's source layout, to which component, to which position etc. To get this information should use next classes:
- LayoutBoundTransferable;
- DDVerticalLayoutTargetDetails.

In the drop method get references to the objects LayoutBoundTransferable and DDVerticalLayoutTargetDetails.

```java
LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();
DDVerticalLayoutTargetDetails details = (DDVerticalLayoutTargetDetails) event.getTargetDetails();
```

LayoutBoundTransferable contains next main methods:
- getSourceComponent() - return the layout from which transferable component was dragged;
- getTransferableComponent() - return transferable component;

```Java
Component sourceLayout = t.getSourceComponent();
Component tComponent = t.getTransferableComponent();
```

DDVerticalLayoutTargetDetails contains information about layout to which drop.
The main methods are:
- getTarget() - return layout to which drop;
- getOverComponent() - return child component of the target layout, to which drop transferable component;
- getDropLocation() - return drop location (Middle, Top, Bottom);
- getOverIndex() - return index of child component;
Get references to the layout to which drop transferable component and VerticalDropLocation:

```Java
DDVerticalLayout targetLayout = (DDVerticalLayout) details.getTarget();
VerticalDropLocation loc = details.getDropLocation();
```

Next we should check whether transferable component is null. It is possible if we drag  highlighted html5 text, as it is not component.

```Java
if (tComponent == null) {
    return;
}
```

Define index of component to which we drop (indexTo) and index of transferable component in the target layout (indexFrom). The last index is necessary if component is replaced in its layout. If component is dragged from another layout indexFrom will be -1.

```Java
int indexTo = details.getOverIndex();
int indexFrom = targetLayout.indexOf(tComponent);
```

Create reference to the object that will be added to the layout and check whether component drag from another layout or it is just reordering in the source layout;

```Java
Component componentToAdd;
if (sourceLayout == targetLayout) {

} else {

}
```

If this condition is performing we should assign the componentToAdd reference to the dragged component. Then we should check whether component has changed his position or not. If the component has not changed its location, then finish method.

```Java
componentToAdd = tComponent;
if (indexFrom == indexTo) {
    return;
}
```

If the component has changed its location we must delete it from its layout. Then check if the index of the component to which dropped more than the index of the previous location, then we need to reduce it by one. This is because of removing component from layout.

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

In case when component drag from another layout it is necessary to create appropriate component for view in dashboard. If the dragged component does not hit any of the components in the layout, we need to add to the end of the list of layout components.

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

Next we should check if the component hit to the child component of layout. If it hit we check drop location.
If location is MIDDLE or BOTTOM, we must increase indexTo by one to add below component to which component was dragged.

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

Launch and check:

![](/sreenshots/6-check-adding.png "Check adding components")  

Note that serial numbers are not assigned to dashboard components. Create next method to fix it:

```Java
public void updateDashboardComponents(DDVerticalLayout layout) {

}
```
Need to set value to all countLabel. For this goal it is necessary to get list of child dashboard components.

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

Now at the end of drop method add follow invoke:

```Java
updateDashboardComponents(targetLayout);
```

It remains to implement removing component from dashboard. Add next code to actionPerfom:

```Java
public void actionPerform(Component component) {
    HBoxLayout hBox = (HBoxLayout) component.getParent();
    GroupBoxLayout groupBox = (GroupBoxLayout) hBox.getParent();
    DDVerticalLayout ddLayout = (DDVerticalLayout) groupBox.getParent();
    ddLayout.remove(groupBox);
    updateDashboardComponents(ddLayout);
}
```

Launch and check serial number updating and removing components.
