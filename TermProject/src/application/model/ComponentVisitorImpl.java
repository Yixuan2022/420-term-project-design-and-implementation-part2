package application.model;

public class ComponentVisitorImpl implements ComponentVisitor {

	public float calculatePrice(Item item) {
		float result = item.getPrice();
		return result;
	};

	public float calculatePrice(ItemContainer container) {
		float result = container.getPrice();
		return result;
	};

	public float calculateMarketValue(Item item) {
		float result = item.getMarketPrice();
		return result;
	}

}