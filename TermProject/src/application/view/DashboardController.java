package application.view;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import application.Main;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.surelyhuman.jdrone.control.physical.tello.TelloDrone;
import application.model.Component;
import application.model.ComponentVisitor;
import application.model.ComponentVisitorImpl;
import application.model.Dashboard;
import application.model.DroneAdapter;
import application.model.DroneAdapterImpl;
import application.model.Item;
import application.model.ItemContainer;


public class DashboardController {
    @FXML
    private TreeView<String> itemTree;

    @FXML
    private RadioButton itemVisitButton;

    @FXML
    private RadioButton scanFarmButton;

    @FXML
    private Button goHomeButton;

    @FXML
    private Label label2;

    @FXML
    private Label labelPrice;

    @FXML
    private Label labelMarketValue;

    @FXML
    private VBox vbox1;

    @FXML
    private AnchorPane windowPanel;


    // Reference to the main application.
    private Main main;

    private Component rootComponent;
    private Component selectedComponent;
    private TreeItem<String> rootItemTree;

	ImageView play = new ImageView(new Image("drone.png"));
	SequentialTransition sequence = null;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public DashboardController() {
    }

    @FXML
    private void initialize() {

    }


    public void componentTraversal(Component component, TreeItem<String> treeNode) {
    	if(component==null)
    		 return;
    	if (component instanceof ItemContainer) {
    		TreeItem<String> node = new TreeItem<String> (component.getName());
    		node.setExpanded(true);
    		if(component.getName()!="root") {
                treeNode.getChildren().add(node);
    		}else {
    			node = treeNode;
    		}
    		ItemContainer container = (ItemContainer) component;
    		for(Component child : container.getComponentList()) {
    			componentTraversal(child, node);
    		}
    	} else if (component instanceof Item) {
    		TreeItem<String> node = new TreeItem<String> (component.getName());
            treeNode.getChildren().add(node);
    	}
    }


    public Component getComponent(String name) {
    	Queue<Component> queue = new LinkedList<>();
    	queue.add(rootComponent);
        Component result = null;
    	while (!queue.isEmpty()) {
    		Component node = queue.remove();
    		if(node.getName().equals(name)) {
    			result = node;
    			break;
    		}
    		if (node instanceof ItemContainer) {
    			ItemContainer container = (ItemContainer) node;
        		for(Component item : container.getComponentList()) {
                    item.setParent(node);
        			queue.add(item);
        		}
    		}
    	}
    	return result;
    }

    public List<Component> getComponentList(Component component) {
    	List<Component> componentList = new ArrayList<Component>();
     	Queue<Component> queue = new LinkedList<>();
    	queue.add(component);
    	while (!queue.isEmpty()) {
     		Component node = queue.remove();
    		componentList.add(node);
    		if (node instanceof ItemContainer) {
    			ItemContainer container = (ItemContainer) node;
        		for(Component item : container.getComponentList()) {
                    item.setParent(node);
        			queue.add(item);
        		}
    		}
    	}
    	return componentList;
    }

    private float calulatePrice(List<Component> componentList) {
    	float result = 0.0f;
    	ComponentVisitor visitor = new ComponentVisitorImpl();
        for(Component comp: componentList) {
        	result = result+ comp.priceAccept(visitor);
        }
    	return result;
    }

    private float calulateMarketValue(List<Component> componentList) {
    	float result = 0.0f;
    	ComponentVisitor visitor = new ComponentVisitorImpl();
        for(Component comp: componentList) {
        	if(comp instanceof Item) {
        		result = result+ comp.marketPriceAccept(visitor);
        	}
        }
    	return result;
    }

    public void drawLayout() {
    	Queue<Component> queue = new LinkedList<>();
    	queue.add(rootComponent);
    	windowPanel.getChildren().clear();
    	while (!queue.isEmpty()) {
    		Component node = queue.remove();
    		 System.out.println("node is:"+node.getName());
    		//draw component
    		if(node.getName().equals("drone")) {
    			drawDrone(node);
    		} else if(!node.getName().equals("root")) {
	            Text text = new Text(node.getName());
	            String name = node.getName();
	            text.setX(node.getLocationX()+(node.getWidth()-name.length()*6)/2);
	            text.setY(node.getLocationY()+10);
	            Rectangle rec = new Rectangle();
	            rec.setX(node.getLocationX());
	            rec.setY(node.getLocationY());
	            rec.setWidth(node.getWidth());
	            rec.setHeight(node.getHeight());
	            rec.setFill(Color.WHITE);
	            rec.setStroke(Color.BLACK);
	            windowPanel.getChildren().add(rec);
	            windowPanel.getChildren().add(text);
    		}
    		//look for children
    		if (node instanceof ItemContainer) {
    			ItemContainer container = (ItemContainer) node;
        		for(Component item : container.getComponentList()) {
                    item.setParent(node);
        			queue.add(item);
        		}
    		}
    	}
    }

	public void drawDrone(Component node) {
	}

	public void itemRadioButtonChanged() {
	    itemVisitButton.setSelected(true);
        scanFarmButton.setSelected(false);
	}

	public void farmRadioButtonChanged() {
	    itemVisitButton.setSelected(false);
        scanFarmButton.setSelected(true);
	}

	public void simulatorButtonChanged() {
		if(itemVisitButton.isSelected()) {
			 startItemVisit();
		}
		if(scanFarmButton.isSelected()) {
			scanFarmDrone();
		}
	}

	public void launchButtonChanged() {
		if(itemVisitButton.isSelected()) {
			 startRealItemVisit();
		}
		if(scanFarmButton.isSelected()) {
			scanRealFarmDrone();
		}
	}


	public void startItemVisit() {
		SequentialTransition sequence = new SequentialTransition();
		Timeline moveDiagonal = new Timeline();

		Duration startDuration = Duration.ZERO;
		Duration endDuration = Duration.seconds(3);

		Component compent = getComponent("Command center");
		int homeValueX = 0;
		int homeValueY = 0;
		if(compent!=null) {
			homeValueX = compent.getLocationX();
			homeValueY = compent.getLocationY();
		}
		compent = selectedComponent;
		int endValueX = 0;
		int endValueY = 0;
		if(compent!=null) {
			endValueX = compent.getLocationX() - homeValueX;
			endValueY = compent.getLocationY() - homeValueY;
		}
		KeyValue endKeyValueX = new KeyValue(play.translateXProperty(), endValueX);
		KeyValue endKeyValueY = new KeyValue(play.translateYProperty(), endValueY);
		KeyFrame endKeyFrameDiagonal = new KeyFrame(endDuration, endKeyValueX, endKeyValueY);
		moveDiagonal = new Timeline(endKeyFrameDiagonal);
		sequence = new SequentialTransition(moveDiagonal);
		sequence.play();
		sequence.setCycleCount(Timeline.INDEFINITE);
	}

	public void startRealItemVisit() {
		Component compent = getComponent("Command center");
		int homeValueX = 0;
		int homeValueY = 0;
		if(compent!=null) {
			homeValueX = compent.getLocationX();
			homeValueY = compent.getLocationY();
		}
		compent = selectedComponent;
		int endValueX = 0;
		int endValueY = 0;
		if(compent!=null) {
			endValueX = compent.getLocationX() - homeValueX;
			endValueY = compent.getLocationY() - homeValueY;
		}
		//lauch drone
		try {
			TelloDrone telloDrone = new TelloDrone();
			DroneAdapter drone = new DroneAdapterImpl(telloDrone);
			drone.activateSDK();
			drone.streamOn();
			drone.streamViewOn();
			drone.hoverInPlace(10);
			drone.takeoff();
			drone.gotoXY(endValueX, endValueY, 10);
			drone.land();
			drone.streamOff();
			drone.streamViewOff();
			drone.end();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void goHomeDrone() {
		Timeline moveDiagonal = new Timeline();
		Duration startDuration = Duration.ZERO;
		Duration endDuration = Duration.seconds(3);
		KeyValue endKeyValueX = new KeyValue(play.translateXProperty(), 0);
		KeyValue endKeyValueY = new KeyValue(play.translateYProperty(), 0);
		KeyFrame endKeyFrameDiagonal = new KeyFrame(endDuration, endKeyValueX, endKeyValueY);
		moveDiagonal = new Timeline(endKeyFrameDiagonal);
		sequence = new SequentialTransition();
		sequence.stop();
		sequence.getChildren().clear();

		sequence.getChildren().addAll(moveDiagonal);
		sequence.play();
		sequence.setCycleCount(Timeline.INDEFINITE);

	}

	public void scanFarmDrone() {
		Duration endDuration1 = Duration.seconds(1);
		Duration endDuration3 = Duration.seconds(3);
		Component compent = getComponent("Command center");
		int homeValueX = 0;
		if(compent!=null) {
			homeValueX = compent.getLocationX();
		}

		Timeline moveDiagonal1  = addTimeLine(0,               0,       -homeValueX,       0,   endDuration1);
		Timeline moveDiagonal2  = addTimeLine(-homeValueX,     0,       -homeValueX,       720, endDuration3);
		Timeline moveDiagonal3  = addTimeLine(-homeValueX,     720,     -homeValueX+200,   720, endDuration1);
		Timeline moveDiagonal4  = addTimeLine(-homeValueX+200, 720,     -homeValueX+200,   0,   endDuration3);
		Timeline moveDiagonal5  = addTimeLine(-homeValueX+200, 0,       -homeValueX+400,   0,   endDuration1);
		Timeline moveDiagonal6  = addTimeLine(-homeValueX+400, 0,       -homeValueX+400,   720, endDuration3);

		Timeline moveDiagonal7  = addTimeLine(-homeValueX+400, 720,     -homeValueX+500,   720, endDuration1);
		Timeline moveDiagonal8  = addTimeLine(-homeValueX+500, 720,     -homeValueX+500,   0,   endDuration3);
		Timeline moveDiagonal13 = addTimeLine(-homeValueX+500, 0,       0,                0,       endDuration1);

		//SequentialTransition sequence = new SequentialTransition();
		sequence = new SequentialTransition();
		sequence.getChildren().addAll(
				moveDiagonal1, moveDiagonal2, moveDiagonal3, moveDiagonal4, moveDiagonal5, moveDiagonal6, moveDiagonal7,
				moveDiagonal8,moveDiagonal13
		        );
		sequence.play();
		sequence.setCycleCount(Timeline.INDEFINITE);
	}


	public void scanRealFarmDrone() {
		Component compent = getComponent("Command center");
		int homeValueX = 0;
		if(compent!=null) {
			homeValueX = compent.getLocationX();
		}
		//lauch drone
		try {
			TelloDrone telloDrone = new TelloDrone();
			DroneAdapter drone = new DroneAdapterImpl(telloDrone);
			drone.takeoff();
			drone.activateSDK();
			drone.streamOn();
			drone.streamViewOn();
			drone.hoverInPlace(10);
			drone.takeoff();
			drone.turnCW(180);
			drone.gotoXY(-homeValueX, 0, 10);
			drone.turnCCW(90);
			drone.gotoXY(-homeValueX, 720, 10);
			drone.turnCCW(90);
			drone.gotoXY(-homeValueX+200, 720, 10);
			drone.turnCCW(90);
			drone.gotoXY(-homeValueX+200, 0, 10);
			drone.turnCW(90);
			drone.gotoXY(-homeValueX+400, 0, 10);
			drone.turnCW(90);
			drone.gotoXY(-homeValueX+400, 720, 10);
			drone.turnCCW(90);
			drone.gotoXY(-homeValueX+500, 720, 10);
			drone.turnCCW(90);
			drone.gotoXY(-homeValueX+500, 0, 10);
			drone.turnCCW(90);
			drone.gotoXY(0, 0, 10);
			drone.land();
			drone.streamOff();
			drone.streamViewOff();
			drone.end();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Timeline addTimeLine(double x0, double y0, double x1, double y1, Duration endDuration) {
		Duration startDuration = Duration.ZERO;
		Timeline moveDiagonal = new Timeline();
		KeyValue keyValueX0 = new KeyValue(play.translateXProperty(), x0);
		KeyValue keyValueY0 = new KeyValue(play.translateYProperty(), y0);
		KeyFrame keyFrameDiagonal0 = new KeyFrame(startDuration, keyValueX0, keyValueY0);
		KeyValue keyValueX1 = new KeyValue(play.translateXProperty(), x1);
		KeyValue keyValueY1 = new KeyValue(play.translateYProperty(), y1);
		KeyFrame keyFrameDiagonal1 = new KeyFrame(endDuration, keyValueX1, keyValueY1);
		moveDiagonal = new Timeline(keyFrameDiagonal0, keyFrameDiagonal1);
		return moveDiagonal;
	}

	public void setMainApp(Main mainApp) {
		this.main = mainApp;
		Dashboard dashboard = Dashboard.getInstance();
		ItemContainer root = new ItemContainer();
		root.setName("root");
		ItemContainer barn = new ItemContainer();
		barn.setName("barn");
		barn.setLocationX(dashboard.getXValue("barn"));
		barn.setLocationY(dashboard.getYValue("barn"));
		barn.setWidth(dashboard.getWValue("barn"));
		barn.setHeight(dashboard.getHValue("barn"));
		barn.setPrice(dashboard.getPriceValue("barn"));
		barn.setMarketPrice(dashboard.getMarketPriceValue("barn"));
		root.addChild(barn);
		Item cow = new Item();
		cow.setName("cow");
		cow.setLocationX(dashboard.getXValue("cow"));
		cow.setLocationY(dashboard.getYValue("cow"));
		cow.setWidth(dashboard.getWValue("cow"));
		cow.setHeight(dashboard.getHValue("cow"));
		cow.setPrice(dashboard.getPriceValue("cow"));
		cow.setMarketPrice(dashboard.getMarketPriceValue("cow"));
		barn.addChild(cow);
		ItemContainer storageBuilding = new ItemContainer();
		storageBuilding.setName("storage building");
		storageBuilding.setLocationX(dashboard.getXValue("storage building"));
		storageBuilding.setLocationY(dashboard.getYValue("storage building"));
		storageBuilding.setWidth(dashboard.getWValue("storage building"));
		storageBuilding.setHeight(dashboard.getHValue("storage building"));
		storageBuilding.setPrice(dashboard.getPriceValue("storage building"));
		storageBuilding.setMarketPrice(dashboard.getMarketPriceValue("storage building"));
		root.addChild(storageBuilding);
		Item tractor = new Item();
		tractor.setName("tractor");
		tractor.setLocationX(dashboard.getXValue("tractor"));
		tractor.setLocationY(dashboard.getYValue("tractor"));
		tractor.setWidth(dashboard.getWValue("tractor"));
		tractor.setHeight(dashboard.getHValue("tractor"));
		tractor.setPrice(dashboard.getPriceValue("tractor"));
		tractor.setMarketPrice(dashboard.getMarketPriceValue("tractor"));
		storageBuilding.addChild(tractor);
		Item tiller = new Item();
		tiller.setName("tiller");
		tiller.setLocationX(dashboard.getXValue("tiller"));
		tiller.setLocationY(dashboard.getYValue("tiller"));
		tiller.setWidth(dashboard.getWValue("tiller"));
		tiller.setHeight(dashboard.getHValue("tiller"));
		tiller.setPrice(dashboard.getPriceValue("tiller"));
		tiller.setMarketPrice(dashboard.getMarketPriceValue("tiller"));
		storageBuilding.addChild(tiller);
		ItemContainer corpField = new ItemContainer();
		corpField.setName("Corp Field");
		corpField.setLocationX(dashboard.getXValue("Corp Field"));
		corpField.setLocationY(dashboard.getYValue("Corp Field"));
		corpField.setWidth(dashboard.getWValue("Corp Field"));
		corpField.setHeight(dashboard.getHValue("Corp Field"));
		corpField.setPrice(dashboard.getPriceValue("Corp Field"));
		corpField.setMarketPrice(dashboard.getMarketPriceValue("Corp Field"));
		root.addChild(corpField);
		Item soyCorp = new Item();
		soyCorp.setName("Soy Corp");
		soyCorp.setLocationX(dashboard.getXValue("Soy Corp"));
		soyCorp.setLocationY(dashboard.getYValue("Soy Corp"));
		soyCorp.setWidth(dashboard.getWValue("Soy Corp"));
		soyCorp.setHeight(dashboard.getHValue("Soy Corp"));
		soyCorp.setPrice(dashboard.getPriceValue("Soy Corp"));
		soyCorp.setMarketPrice(dashboard.getMarketPriceValue("Soy Corp"));
		corpField.addChild(soyCorp);
		ItemContainer commandCenter = new ItemContainer();
		commandCenter.setName("Command center");
		commandCenter.setLocationX(dashboard.getXValue("Command center"));
		commandCenter.setLocationY(dashboard.getYValue("Command center"));
		commandCenter.setWidth(dashboard.getWValue("Command center"));
		commandCenter.setHeight(dashboard.getHValue("Command center"));
		commandCenter.setPrice(dashboard.getPriceValue("Command center"));
		commandCenter.setMarketPrice(dashboard.getMarketPriceValue("Command center"));
		root.addChild(commandCenter);
		Item drone = new Item();
		drone.setName("drone");
		drone.setLocationX(dashboard.getXValue("drone"));
		drone.setLocationY(dashboard.getYValue("drone"));
		drone.setWidth(dashboard.getWValue("drone"));
		drone.setHeight(dashboard.getHValue("drone"));
		commandCenter.addChild(drone);

		TreeItem<String> rootItem = new TreeItem<String>(root.getName());
		componentTraversal(root, rootItem);
		itemTree.setRoot(rootItem); // rendering the tree

		this.rootComponent = root;
		this.rootItemTree = rootItem;

		setUpTreeViewListener();

		drawLayout();

        //Init drone
		Component compent = getComponent("Command center");
		int homeValueX = 0;
		int homeValueY = 0;
		if (compent != null) {
			homeValueX = compent.getLocationX();
			homeValueY = compent.getLocationY();
		}
		play.setX(homeValueX);
		play.setY(homeValueY);

		windowPanel.getChildren().add(play);

	}

    public void  setUpTreeViewListener() {
         //set up listener in  tree view
          MultipleSelectionModel<TreeItem<String>> itemSelModel = itemTree.getSelectionModel();
          itemSelModel.selectedItemProperty().addListener( new ChangeListener() {

             @Override
             public void changed(ObservableValue observable, Object oldValue,
                     Object newValue) {
                 TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                 String selectedItemValue = "root";
                 if(selectedItem!=null) {
                	 selectedItemValue = selectedItem.getValue();
                 }
                 System.out.println("Selected Text : " + selectedItemValue);

                 //set up comands
                 vbox1.getChildren().clear();
                 Component component = getComponent(selectedItemValue);
                 selectedComponent = component;
            	 Button button1 = new Button("Item Commands");
            	 button1.setMinWidth(vbox1.getPrefWidth());
            	 Button button2 = new Button("Item Container Commands");
            	 button2.setMinWidth(vbox1.getPrefWidth());
                 Button button3 = new Button("Rename");
                 button3.setMinWidth(vbox1.getPrefWidth());
                 Button button4 = new Button("Change Location");
                 button4.setMinWidth(vbox1.getPrefWidth());
                 Button button5 = new Button("Change Price");
                 button5.setMinWidth(vbox1.getPrefWidth());
                 Button button6 = new Button("Change Demensions");
                 button6.setMinWidth(vbox1.getPrefWidth());
                 Button button7 = new Button("Add Item");
                 button7.setMinWidth(vbox1.getPrefWidth());
                 Button button8 = new Button("Add Item Container");
                 button8.setMinWidth(vbox1.getPrefWidth());
                 Button button9 = new Button("Delete");
                 button9.setMinWidth(vbox1.getPrefWidth());

                 if(component instanceof Item) {
                	 vbox1.getChildren().add(button1);
                     vbox1.getChildren().add(button3);
                     vbox1.getChildren().add(button4);
                     vbox1.getChildren().add(button5);
                     vbox1.getChildren().add(button6);
                     vbox1.getChildren().add(button9);
                 } else if (component instanceof ItemContainer) {
                	 vbox1.getChildren().add(button2);
                     vbox1.getChildren().add(button3);
                     vbox1.getChildren().add(button4);
                     vbox1.getChildren().add(button5);
                     vbox1.getChildren().add(button6);
                     vbox1.getChildren().add(button7);
                     vbox1.getChildren().add(button8);
                     vbox1.getChildren().add(button9);
                 }

                 //implement Visitor pattern: calcuate price and value
                 List<Component> componentList = getComponentList(component);
                 float price = calulatePrice(componentList);
                 labelPrice.setText(String.valueOf(price));
                 float marketValue = calulateMarketValue(componentList);
                 labelMarketValue.setText(String.valueOf(marketValue));

                 // Name
                 final Stage myDialog0 = new Stage();
                 myDialog0.initModality(Modality.WINDOW_MODAL);

                 //change name
                 button3.setOnAction(new EventHandler<ActionEvent>(){
                     @Override
                     public void handle(ActionEvent arg0) {
                    	 Stage newStage = new Stage();
                    	 Component compent = selectedComponent;

                    	 GridPane grid = new GridPane();
                    	 grid.setAlignment(Pos.CENTER);
                    	 grid.setHgap(10);
                    	 grid.setVgap(10);
                    	 grid.setPadding(new Insets(25, 25, 25, 25));

                    	 Text scenetitle = new Text("Enter new Name:");
                    	 scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                    	 grid.add(scenetitle, 0, 0, 2, 1);

                    	 Label nameLabel = new Label("New Name");
                    	 grid.add(nameLabel, 0, 1);

                    	 TextField nameTextField = new TextField(compent.getName());
                    	 grid.add(nameTextField, 1, 1);

                    	 Button cancelBtn = new Button("Cancel");
                    	 HBox hbBtn0 = new HBox(10);
                    	 hbBtn0.setAlignment(Pos.BOTTOM_RIGHT);
                    	 hbBtn0.getChildren().add(cancelBtn);
                    	 grid.add(hbBtn0, 0, 4);

                    	 Button okBtn = new Button("OK");
                    	 HBox hbBtn = new HBox(10);
                    	 hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                    	 hbBtn.getChildren().add(okBtn);
                    	 grid.add(hbBtn, 1, 4);

                    	 final Text actiontarget = new Text();
                         grid.add(actiontarget, 1, 6);

                         okBtn.setOnAction(new EventHandler<ActionEvent>() {
                         	    @Override
                        	    public void handle(ActionEvent e) {
                        	        actiontarget.setFill(Color.FIREBRICK);
                        	        actiontarget.setText(nameTextField.getText().toString());
                        	        String newName = nameTextField.getText().toString();
                        	        selectedComponent.rename(newName);
                                    //refresh tree view
                           	        TreeItem<String> rootItem = new TreeItem<String> ("root");
                        	        componentTraversal(rootComponent, rootItem);
                        	        rootItemTree = rootItem;
                                    itemTree.setRoot(rootItem);   //rendering tree
                                    drawLayout();
                                    newStage.close();
                        	    }
                         });

                         cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                      	    @Override
                     	    public void handle(ActionEvent e) {
                      	    	newStage.close();
                     	    }
                         });

                    	 Scene scene = new Scene(grid, 300, 275);
                    	 newStage.setScene(scene);
                    	 newStage.show();
                     }
                 });

                 //Location
                 button4.setOnAction(new EventHandler<ActionEvent>(){
                     @Override
                     public void handle(ActionEvent arg0) {
                    	 Stage newStage = new Stage();
                    	 Component compent = selectedComponent;

                    	 GridPane grid = new GridPane();
                    	 grid.setAlignment(Pos.CENTER);
                    	 grid.setHgap(10);
                    	 grid.setVgap(10);
                    	 grid.setPadding(new Insets(25, 25, 25, 25));

                    	 Text scenetitle = new Text("Enter new location:");
                    	 scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                    	 grid.add(scenetitle, 0, 0, 2, 1);

                    	 Label xLabel = new Label("Location X");
                    	 grid.add(xLabel, 0, 1);

                    	 TextField xTextField = new TextField(String.valueOf(compent.getLocationX()));
                    	 grid.add(xTextField, 1, 1);

                    	 Label yLabel = new Label("Location Y");
                    	 grid.add(yLabel, 0, 2);

                    	 TextField yTextField = new TextField(String.valueOf(compent.getLocationY()));
                    	 grid.add(yTextField, 1, 2);


                    	 Button cancelBtn = new Button("Cancel");
                    	 HBox hbBtn0 = new HBox(10);
                    	 hbBtn0.setAlignment(Pos.BOTTOM_RIGHT);
                    	 hbBtn0.getChildren().add(cancelBtn);
                    	 grid.add(hbBtn0, 0, 4);

                    	 Button okBtn = new Button("OK");
                    	 HBox hbBtn = new HBox(10);
                    	 hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                    	 hbBtn.getChildren().add(okBtn);
                    	 grid.add(hbBtn, 1, 4);

                    	 final Text actiontarget = new Text();
                         grid.add(actiontarget, 1, 6);

                         okBtn.setOnAction(new EventHandler<ActionEvent>() {
                         	    @Override
                        	    public void handle(ActionEvent e) {
                        	        actiontarget.setFill(Color.FIREBRICK);
                        	        actiontarget.setText(xTextField.getText().toString());
                        	        Integer x = Integer.valueOf(xTextField.getText().toString());
                        	        selectedComponent.setLocationX(x);
                        	        Integer y = Integer.valueOf(yTextField.getText().toString());
                        	        selectedComponent.setLocationY(y);
                        	        drawLayout();
                        	        newStage.close();
                        	    }
                         });

                         cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                      	    @Override
                     	    public void handle(ActionEvent e) {
                      	    	newStage.close();
                     	    }
                         });

                    	 Scene scene = new Scene(grid, 300, 275);
                    	 newStage.setScene(scene);
                    	 newStage.show();
                     }

                 });

                 //Price
                 button5.setOnAction(new EventHandler<ActionEvent>(){
                     @Override
                     public void handle(ActionEvent arg0) {
                        	 Stage newStage = new Stage();
                        	 Component compent = selectedComponent;

                        	 GridPane grid = new GridPane();
                        	 grid.setAlignment(Pos.CENTER);
                        	 grid.setHgap(10);
                        	 grid.setVgap(10);
                        	 grid.setPadding(new Insets(25, 25, 25, 25));

                        	 Text scenetitle = new Text("Enter new price:");
                        	 scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                        	 grid.add(scenetitle, 0, 0, 2, 1);

                        	 Label priceLabel = new Label("New Purchase Price");
                        	 grid.add(priceLabel, 0, 1);

                        	 TextField priceTextField = new TextField(String.valueOf(compent.getPrice()));
                        	 grid.add(priceTextField, 1, 1);

                        	 Label marketPriceLabel = new Label("New Market Price");
                        	 grid.add(marketPriceLabel, 0, 2);

                        	 TextField marketPriceTextField = new TextField(String.valueOf(compent.getMarketPrice()));
                        	 grid.add(marketPriceTextField, 1, 2);

                        	 Button cancelBtn = new Button("Cancel");
                        	 HBox hbBtn0 = new HBox(10);
                        	 hbBtn0.setAlignment(Pos.BOTTOM_RIGHT);
                        	 hbBtn0.getChildren().add(cancelBtn);
                        	 grid.add(hbBtn0, 0, 4);

                        	 Button okBtn = new Button("OK");
                        	 HBox hbBtn = new HBox(10);
                        	 hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                        	 hbBtn.getChildren().add(okBtn);
                        	 grid.add(hbBtn, 1, 4);

                        	 final Text actiontarget = new Text();
                             grid.add(actiontarget, 1, 6);

                             okBtn.setOnAction(new EventHandler<ActionEvent>() {
                             	    @Override
                            	    public void handle(ActionEvent e) {
                            	        actiontarget.setFill(Color.FIREBRICK);
                            	        actiontarget.setText(priceTextField.getText().toString());
                            	        String price = priceTextField.getText().toString();
                                        String marketPrice = marketPriceTextField.getText().toString();
                            	        selectedComponent.setPrice(Float.valueOf(price));
                            	        selectedComponent.setMarketPrice(Float.valueOf(marketPrice));
                            	        newStage.close();
                            	    }
                             });

                             cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                          	    @Override
                         	    public void handle(ActionEvent e) {
                          	    	newStage.close();
                         	    }
                             });

                        	 Scene scene = new Scene(grid, 300, 275);
                        	 newStage.setScene(scene);
                        	 newStage.show();
                         }

                 });

                 //Demensions
                 button6.setOnAction(new EventHandler<ActionEvent>(){
                     @Override
                     public void handle(ActionEvent arg0) {
                    	 Stage newStage = new Stage();
                    	 Component compent = selectedComponent;

                    	 GridPane grid = new GridPane();
                    	 grid.setAlignment(Pos.CENTER);
                    	 grid.setHgap(10);
                    	 grid.setVgap(10);
                    	 grid.setPadding(new Insets(25, 25, 25, 25));

                    	 Text scenetitle = new Text("Enter new demensions:");
                    	 scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                    	 grid.add(scenetitle, 0, 0, 2, 1);

                    	 Label lLabel = new Label("length");
                    	 grid.add(lLabel, 0, 1);

                    	 TextField lTextField = new TextField(String.valueOf(compent.getLength()));
                    	 grid.add(lTextField, 1, 1);

                    	 Label wLabel = new Label("width");
                    	 grid.add(wLabel, 0, 2);

                    	 TextField wTextField = new TextField(String.valueOf(compent.getWidth()));
                    	 grid.add(wTextField, 1, 2);

                    	 Label hLabel = new Label("height");
                    	 grid.add(hLabel, 0, 3);

                    	 TextField hTextField = new TextField(String.valueOf(compent.getHeight()));
                    	 grid.add(hTextField, 1, 3);


                    	 Button cancelBtn = new Button("Cancel");
                    	 HBox hbBtn0 = new HBox(10);
                    	 hbBtn0.setAlignment(Pos.BOTTOM_RIGHT);
                    	 hbBtn0.getChildren().add(cancelBtn);
                    	 grid.add(hbBtn0, 0, 4);

                    	 Button okBtn = new Button("OK");
                    	 HBox hbBtn = new HBox(10);
                    	 hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                    	 hbBtn.getChildren().add(okBtn);
                    	 grid.add(hbBtn, 1, 4);

                    	 final Text actiontarget = new Text();
                         grid.add(actiontarget, 1, 6);

                         okBtn.setOnAction(new EventHandler<ActionEvent>() {
                         	    @Override
                        	    public void handle(ActionEvent e) {
                        	        actiontarget.setFill(Color.FIREBRICK);
                        	        actiontarget.setText(lTextField.getText().toString());
                        	        Integer l = Integer.valueOf(lTextField.getText().toString());
                        	        selectedComponent.setLength(l);
                        	        Integer w = Integer.valueOf(wTextField.getText().toString());
                        	        selectedComponent.setWidth(w);
                        	        Integer h = Integer.valueOf(hTextField.getText().toString());
                        	        selectedComponent.setHeight(w);
                        	        drawLayout();
                        	        newStage.close();
                        	    }
                         });

                         cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                      	    @Override
                     	    public void handle(ActionEvent e) {
                      	    	newStage.close();
                     	    }
                         });

                    	 Scene scene = new Scene(grid, 300, 275);
                    	 newStage.setScene(scene);
                    	 newStage.show();
                     }

                 });

                 //Add Item
                 button7.setOnAction(new EventHandler<ActionEvent>(){
                     @Override
                     public void handle(ActionEvent arg0) {
                        	 Stage newStage = new Stage();
                        	 Component compent = selectedComponent;

                        	 GridPane grid = new GridPane();
                        	 grid.setAlignment(Pos.CENTER);
                        	 grid.setHgap(20);
                        	 grid.setVgap(10);
                        	 grid.setPadding(new Insets(25, 25, 25, 25));

                        	 Text scenetitle = new Text("Enter the name of the new item:");
                        	 scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                        	 grid.add(scenetitle, 0, 0, 2, 1);

                        	 TextField itemTextField = new TextField("");
                        	 itemTextField.setMinWidth(300);
                        	 grid.add(itemTextField, 0, 1,4,1);

                        	 Button cancelBtn = new Button("Cancel");
                        	 HBox hbBtn0 = new HBox(10);
                        	 hbBtn0.setAlignment(Pos.BOTTOM_RIGHT);
                        	 hbBtn0.getChildren().add(cancelBtn);
                        	 grid.add(hbBtn0, 0, 2,1,1);

                        	 Button okBtn = new Button("OK");
                        	 HBox hbBtn = new HBox(10);
                        	 hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                        	 hbBtn.getChildren().add(okBtn);
                        	 grid.add(hbBtn, 1, 2,1,1);

                        	 final Text actiontarget = new Text();
                             grid.add(actiontarget, 1, 6);

                             okBtn.setOnAction(new EventHandler<ActionEvent>() {
                             	    @Override
                            	    public void handle(ActionEvent e) {
                            	        actiontarget.setFill(Color.FIREBRICK);
                            	        actiontarget.setText(itemTextField.getText().toString());
                            	        String itemName = itemTextField.getText().toString();
                            	        Item item = new Item();
                            	        item.setName(itemName);
                                        ItemContainer container = (ItemContainer)selectedComponent;
                                        container.getComponentList().add(item);
                                        //refresh tree view
                               	        TreeItem<String> rootItem = new TreeItem<String> ("root");
                            	        componentTraversal(rootComponent, rootItem);
                            	        rootItemTree = rootItem;
                                        itemTree.setRoot(rootItem);   //rendering tree
                                        newStage.close();
                            	    }
                             });

                             cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                          	    @Override
                         	    public void handle(ActionEvent e) {
                          	    	newStage.close();
                         	    }
                             });

                        	 Scene scene = new Scene(grid, 400, 275);
                        	 newStage.setScene(scene);
                        	 newStage.show();
                         }

                 });

                 //Add Item Container
                 button8.setOnAction(new EventHandler<ActionEvent>(){
                     @Override
                     public void handle(ActionEvent arg0) {
                        	 Stage newStage = new Stage();
                        	 Component compent = selectedComponent;

                        	 GridPane grid = new GridPane();
                        	 grid.setAlignment(Pos.CENTER);
                        	 grid.setHgap(20);
                        	 grid.setVgap(10);
                        	 grid.setPadding(new Insets(25, 25, 25, 25));

                        	 Text scenetitle = new Text("Enter the name of the new item container:");
                        	 scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
                        	 grid.add(scenetitle, 0, 0, 2, 1);

                        	 TextField itemTextField = new TextField("");
                        	 itemTextField.setMinWidth(300);
                        	 grid.add(itemTextField, 0, 1,4,1);

                        	 Button cancelBtn = new Button("Cancel");
                        	 HBox hbBtn0 = new HBox(10);
                        	 hbBtn0.setAlignment(Pos.BOTTOM_RIGHT);
                        	 hbBtn0.getChildren().add(cancelBtn);
                        	 grid.add(hbBtn0, 0, 2,1,1);

                        	 Button okBtn = new Button("OK");
                        	 HBox hbBtn = new HBox(10);
                        	 hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                        	 hbBtn.getChildren().add(okBtn);
                        	 grid.add(hbBtn, 1, 2,1,1);

                        	 final Text actiontarget = new Text();
                             grid.add(actiontarget, 1, 6);

                             okBtn.setOnAction(new EventHandler<ActionEvent>() {
                             	    @Override
                            	    public void handle(ActionEvent e) {
                            	        actiontarget.setFill(Color.FIREBRICK);
                            	        actiontarget.setText(itemTextField.getText().toString());
                            	        String itemName = itemTextField.getText().toString();
                            	        ItemContainer newCont = new ItemContainer();
                                        ItemContainer container = (ItemContainer)selectedComponent;
                                        container.getComponentList().add(newCont);
                                        //refresh tree view
                               	        TreeItem<String> rootItem = new TreeItem<String> ("root");
                            	        componentTraversal(rootComponent, rootItem);
                            	        rootItemTree = rootItem;
                                        itemTree.setRoot(rootItem);   //rendering tree
                                        newStage.close();
                            	    }
                             });

                             cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                          	    @Override
                         	    public void handle(ActionEvent e) {
                          	    	newStage.close();
                         	    }
                             });

                        	 Scene scene = new Scene(grid, 400, 275);
                        	 newStage.setScene(scene);
                        	 newStage.show();
                         }

                 });

                 //Delete
                 button9.setOnAction(new EventHandler<ActionEvent>(){
                     @Override
                     public void handle(ActionEvent arg0) {
                        	 Component compont = selectedComponent;
                        	 Component parent = selectedComponent.getParent();
                        	 ItemContainer container = (ItemContainer)parent;
                        	 container.getComponentList().remove(component);
                    	     //refresh tree view
                        	 TreeItem<String> rootItem = new TreeItem<String> ("root");
                 	         componentTraversal(rootComponent, rootItem);
                 	         rootItemTree = rootItem;
                             itemTree.setRoot(rootItem);   //rendering tree
                      }
                 });

             }

           });

    }

}
