package application.model;

public interface ComponentVisitor {

	public float calculatePrice(Item item);
	public float calculatePrice(ItemContainer container);
	public float calculateMarketValue(Item item);

}