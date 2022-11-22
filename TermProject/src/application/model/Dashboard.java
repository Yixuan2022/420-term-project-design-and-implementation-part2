package application.model;

import java.util.HashMap;

public class Dashboard {

	private static Dashboard instance;

	private String name;

    static HashMap<String, Integer> xmap = new HashMap<String, Integer>();
    static HashMap<String, Integer> ymap = new HashMap<String, Integer>();
    static HashMap<String, Integer> wmap = new HashMap<String, Integer>();
    static HashMap<String, Integer> hmap = new HashMap<String, Integer>();
    static HashMap<String, Float> pricemap = new HashMap<String, Float>();
    static HashMap<String, Float> marketpricemap = new HashMap<String, Float>();

	private Dashboard() {
	}

	public static Dashboard getInstance() {
		if (instance == null) {
			instance = new Dashboard();
			intMap();
		}
		return instance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getXValue(String name) {
		Integer x =  (Integer) xmap.get(name);
		return x;
	}

	public Integer getYValue(String name) {
		Integer y =  (Integer) ymap.get(name);
		return y;
	}

	public Integer getWValue(String name) {
		Integer y =  (Integer) wmap.get(name);
		return y;
	}

	public Integer getHValue(String name) {
		Integer y =  (Integer) hmap.get(name);
		return y;
	}

	public Float getPriceValue(String name) {
		Float y = 0.0f;
	    if(pricemap.get(name)!=null) {
	    	y =  pricemap.get(name);
	    }
		return y;
	}

	public Float getMarketPriceValue(String name) {
		Float y = 0.0f;
	    if(marketpricemap.get(name)!=null) {
	    	y =  marketpricemap.get(name);
	    }
		return y;
	}

	private static void intMap() {
		xmap.put("barn", 60);
		ymap.put("barn", 100);
		wmap.put("barn", 150);
		hmap.put("barn", 125);
		pricemap.put("barn", 10.0f);
		marketpricemap.put("barn", 10.0f);

		xmap.put("cow", 80);
		ymap.put("cow", 120);
		wmap.put("cow", 50);
		hmap.put("cow", 50);
		pricemap.put("cow", 20.0f);
		marketpricemap.put("cow", 20.0f);

		xmap.put("storage building", 300);
		ymap.put("storage building", 100);
		wmap.put("storage building", 150);
		hmap.put("storage building", 150);
		pricemap.put("storage building", 30.0f);
		marketpricemap.put("storage building", 30.0f);

		xmap.put("tractor", 350);
		ymap.put("tractor", 120);
		wmap.put("tractor", 50);
		hmap.put("tractor", 50);
		pricemap.put("tractor", 40.0f);
		marketpricemap.put("tractor", 40.0f);

		xmap.put("tiller", 350);
		ymap.put("tiller", 200);
		wmap.put("tiller", 30);
		hmap.put("tiller", 30);
		pricemap.put("tiller", 50.0f);
		marketpricemap.put("tiller", 50.0f);

		xmap.put("Corp Field", 100);
		ymap.put("Corp Field", 500);
		wmap.put("Corp Field", 300);
		hmap.put("Corp Field", 180);
		pricemap.put("Corp Field", 60.0f);
		marketpricemap.put("Corp Field", 60.0f);

		xmap.put("Soy Corp", 100);
		ymap.put("Soy Corp", 500);
		wmap.put("Soy Corp", 100);
		hmap.put("Soy Corp", 180);
		pricemap.put("Soy Corp", 70.0f);
		marketpricemap.put("Soy Corp", 70.0f);

		xmap.put("Command center", 220);
		ymap.put("Command center", 0);
		wmap.put("Command center", 120);
		hmap.put("Command center", 50);
		pricemap.put("Command center", 80.0f);
		marketpricemap.put("Command center", 80.0f);

		xmap.put("drone", 220);
		ymap.put("drone", 0);
		wmap.put("drone", 120);
		hmap.put("drone", 50);
		pricemap.put("drone", 90.0f);
		marketpricemap.put("drone", 90.0f);
	}


}
