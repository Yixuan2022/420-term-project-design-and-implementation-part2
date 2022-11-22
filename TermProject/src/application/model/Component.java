package application.model;

public interface Component {
	public void rename(String newName);
	public String getName();
	public void delete(Component item);

	public int getLocationX();
	public void setLocationX(int locationX);
	public int getLocationY();
	public void setLocationY(int locationY);
	public float getPrice();
	public void setPrice(float price);
	public float getMarketPrice();
	public void setMarketPrice(float marketPrice);
	public int getLength();
	public void setLength(int length);
	public int getWidth() ;
	public void setWidth(int width) ;
	public int getHeight() ;
	public void setHeight(int height) ;

	public Component getParent();
	public void setParent(Component parent);

	public float priceAccept(ComponentVisitor visitor);
	public float marketPriceAccept(ComponentVisitor visitor);

}