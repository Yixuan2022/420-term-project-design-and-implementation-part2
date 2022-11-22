package application.model;

import java.util.ArrayList;
import java.util.List;

public class ItemContainer  implements Component{
    String name;
	float price;
	float marketPrice;
	int locationX;
	int locationY;
	int length;
	int width;
	int height;
	Component parent;
	List<Component> componentList = new ArrayList<Component>();

	public void rename(String newName) {
		this.name = newName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(float marketPrice) {
		this.marketPrice = marketPrice;
	}

	public int getLocationX() {
		return locationX;
	}

	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}

	public int getLocationY() {
		return locationY;
	}

	public void setLocationY(int locationY) {
		this.locationY = locationY;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void delete(Component item){
		componentList.remove(item);
	}

	public void deleteChild(Component item){
		componentList.remove(item);
	}

	public void addChild(Component item){
		componentList.add(item);
	}

	public Component getParent() {
		return parent;
	}

	public void setParent(Component parent) {
		this.parent = parent;
	}

	public List<Component> getComponentList() {
		return componentList;
	}

	public void setComponentList(List<Component> componentList) {
		this.componentList = componentList;
	}

	public float priceAccept(ComponentVisitor visitor) {
		float result = visitor.calculatePrice(this);
		return result;
	};

	public float marketPriceAccept(ComponentVisitor visitor) {
		return 0f;
	};

}
