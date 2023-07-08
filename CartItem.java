
//Name: Vincent Bercze
//Student ID: 501105440


public class CartItem 
{
	Product p;
	String productOptions;
	
	public CartItem(Product p, String productOptions)
	{
		this.p = p;
		this.productOptions = productOptions;
	}
	
	public String getProductOptions()
	{
		return productOptions;
	}
	
	public Product getProduct()
	{
		return p;
	}
}


