import java.awt.List;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

//Name: Vincent Bercze
//Student ID: 501105440


/*
 * Models a simple ECommerce system. Keeps track of products for sale, registered customers, product orders and
 * orders that have been shipped to a customer
 */
public class ECommerceSystem
{
	Map<String, Product>  products = new TreeMap<String, Product>();
	Map<Product, Integer>  productsStats = new HashMap<Product, Integer>();
	ArrayList<Customer> customers = new ArrayList<Customer>();
	
	//arraylist that stores the products which can be used to sort more conveniently
	ArrayList<Product> tempProducts = new ArrayList<Product>();
	
	ArrayList<ProductOrder> orders = new ArrayList<ProductOrder>();
	ArrayList<ProductOrder> shippedOrders = new ArrayList<ProductOrder>();

	// These variables are used to generate order numbers, customer id's, product id's 
	int orderNumber = 500;
	int customerId = 900;
	int productId = 700;


	// Random number generator
	Random random = new Random();
	
	//exceptions that will be thrown based on the error (replaces error message)
	UnknownCustomerException custs;
	UnknownProductException prods;
	InvalidOrderNumberException ordr;
	InvalidProductOptionsException invalidprods;
	ProductOutOfStockException prodstocks;
	InvalidCustomerNameException custname;
	InvalidCustomerAddressException addr;

	public ECommerceSystem()
	{
		// executes the readProducts method, if an exception is caught, the system will print out the error message and exit
		try
		{
			readProducts();
			
			for (int i = 0; i < tempProducts.size(); i++)
			{
				//maps product IDs to product objects
				products.put(tempProducts.get(i).getId(), tempProducts.get(i));
				
				//maps product IDs to the number of times a product was ordered (by default is 0)
				productsStats.put(tempProducts.get(i), 0);
			}
		}
		
		catch (Exception e) 
		{
			System.out.println(e);
			System.exit(1);
		}
		
		
		
		// Create some customers
		String custIDs = "";
		
		customers.add(new Customer(custIDs = generateCustomerId(),"Inigo Montoya", "1 SwordMaker Lane, Florin", new Cart(custIDs)));
		customers.add(new Customer(custIDs = generateCustomerId(),"Prince Humperdinck", "The Castle, Florin", new Cart(custIDs)));
		customers.add(new Customer(custIDs = generateCustomerId(),"Andy Dufresne", "Shawshank Prison, Maine", new Cart(custIDs)));
		customers.add(new Customer(custIDs = generateCustomerId(),"Ferris Bueller", "4160 Country Club Drive, Long Beach", new Cart(custIDs)));
	}
	
	private ArrayList<Product> readProducts() throws IOException
	{
			//Read the products the products.txt file
			File prods = new File("products.txt");
		
			//Scanner reads products.txt file
			Scanner s = new Scanner(prods);	
			
			//variables that will store all the data from the products file in order to insert them into the array list
			String category = "";
			String name = "";
			String price = "";
			String stock = "";
			String bookStocks = "";
			String bookInfo = "";
			
			while (s.hasNext())
			{
				category = s.nextLine();
			
				name = s.nextLine();
				price = s.nextLine();
				
				if (category.equals("BOOKS"))
				{
					bookStocks = s.nextLine();
					String[] s1 = bookStocks.split(" ");
					
					bookInfo = s.nextLine();
					String[] s2 = bookInfo.split(":");
					
					tempProducts.add(new Book(name, generateProductId(), Double.parseDouble(price), Integer.parseInt(s1[0]), Integer.parseInt(s1[1]), s2[0], s2[1], Integer.parseInt(s2[2])));
				}
				
				else if (category.equals("GENERAL"))
				{
					stock = s.nextLine();
					bookInfo = s.nextLine();
					tempProducts.add(new Product(name, generateProductId(), Double.parseDouble(price), Integer.parseInt(stock), Product.Category.GENERAL));
				}
				
				else if (category.equals("CLOTHING"))
				{
					stock = s.nextLine();
					bookInfo = s.nextLine();
					tempProducts.add(new Product(name, generateProductId(), Double.parseDouble(price), Integer.parseInt(stock), Product.Category.CLOTHING));
				}
				
				else if (category.equals("COMPUTERS"))
				{
					stock = s.nextLine();
					bookInfo = s.nextLine();
					tempProducts.add(new Product(name, generateProductId(), Double.parseDouble(price), Integer.parseInt(stock), Product.Category.COMPUTERS));
				}
				
				else if (category.equals("FURNITURE"))
				{
					stock = s.nextLine();
					bookInfo = s.nextLine();
					tempProducts.add(new Product(name, generateProductId(), Double.parseDouble(price), Integer.parseInt(stock), Product.Category.FURNITURE));
				}
				
			}
		
			return tempProducts;
		
	}
	
	private String generateOrderNumber()
	{
		return "" + orderNumber++;
	}

	private String generateCustomerId()
	{
		return "" + customerId++;
	}

	private String generateProductId()
	{
		return "" + productId++;
	}

	public void printAllProducts()
	{
		for (Product p : tempProducts)
			p.print();
	}

	public void printAllBooks()
	{
		for (Product p : tempProducts)
		{
			if (p.getCategory() == Product.Category.BOOKS)
				p.print();
		}
	}

	public ArrayList<Book> booksByAuthor(String author)
	{
		ArrayList<Book> books = new ArrayList<Book>();
		for (Product p : tempProducts)
		{
			if (p.getCategory() == Product.Category.BOOKS)
			{
				Book book = (Book) p;
				if (book.getAuthor().equals(author))
					books.add(book);
			}
		}
		return books;
	}

	public void printAllOrders()
	{
		for (ProductOrder o : orders)
			o.print();
	}

	public void printAllShippedOrders()
	{
		for (ProductOrder o : shippedOrders)
			o.print();
	}

	public void printCustomers()
	{
		for (Customer c : customers)
			c.print();
	}
	/*
	 * Given a customer id, print all the current orders and shipped orders for them (if any)
	 */
	public void printOrderHistory(String customerId) throws Exception
	{
		// Make sure customer exists
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			custs = new UnknownCustomerException("Customer: " + customerId + " Not Found");
			throw custs;
		}	
		System.out.println("Current Orders of Customer " + customerId);
		for (ProductOrder order: orders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		System.out.println("\nShipped Orders of Customer " + customerId);
		for (ProductOrder order: shippedOrders)
		{
			if (order.getCustomer().getId().equals(customerId))
				order.print();
		}
		
	}

	public String orderProduct(String productId, String customerId, String productOptions) throws Exception
	{
		// Get customer
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			custs = new UnknownCustomerException("Customer: " + customerId + " Not Found");
			throw custs;
		}
		Customer customer = customers.get(index);

		// Get product 
		if (products.get(productId) == null)
		{
			prods = new UnknownProductException("Product " + productId + " Not Found");
			throw prods;
		}
		Product product = products.get(productId);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(productOptions))
		{
			invalidprods = new InvalidProductOptionsException("Product " + product.getName() + " ProductId " + productId + " Invalid Options: " + productOptions);
			throw invalidprods;
		}
		// Is it in stock?
		if (product.getStockCount(productOptions) == 0)
		{
			prodstocks = new ProductOutOfStockException("Product " + product.getName() + " ProductId " + productId + " Out of Stock");
			throw prodstocks;
		}
		// Create a ProductOrder
		ProductOrder order = new ProductOrder(generateOrderNumber(), product, customer, productOptions);
		product.reduceStockCount(productOptions);

		// Add to orders and return
		orders.add(order);
		
		//increments the value mapped to the productID key in order to keep track of how many times a particualr product was ordered
		for (Product p : tempProducts)
		{
			if (p.getId().equals(productId))
				productsStats.merge(p, 1, Integer::sum);
		}
		
		return order.getOrderNumber();
	}

	/*
	 * Create a new Customer object and add it to the list of customers
	 */

	public void createCustomer(String name, String address) throws Exception
	{
		// Check to ensure name is valid
		if (name == null || name.equals(""))
		{
			custname = new InvalidCustomerNameException("Invalid Customer Name " + name);
			throw custname;
		}
		// Check to ensure address is valid
		if (address == null || address.equals(""))
		{
			addr = new InvalidCustomerAddressException("Invalid Customer Address " + address);
			throw addr;
		}
		
		String custIDS = generateCustomerId();
		Customer customer = new Customer(custIDS, name, address, new Cart(custIDS));
		customers.add(customer);
	}

	public ProductOrder shipOrder(String orderNumber) throws Exception
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			ordr = new InvalidOrderNumberException("Order " + orderNumber + " Not Found");
			throw ordr;
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
		shippedOrders.add(order);
		return order;
	}

	/*
	 * Cancel a specific order based on order number
	 */
	public void cancelOrder(String orderNumber) throws Exception
	{
		// Check if order number exists
		int index = orders.indexOf(new ProductOrder(orderNumber,null,null,""));
		if (index == -1)
		{
			ordr = new InvalidOrderNumberException("Order " + orderNumber + " Not Found");
			throw ordr;
		}
		ProductOrder order = orders.get(index);
		orders.remove(index);
	}

	// Sort products by increasing price
	public void printByPrice()
	{
		Collections.sort(tempProducts, new PriceComparator());
		printAllProducts();
	}

	private class PriceComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			if (a.getPrice() > b.getPrice()) return 1;
			if (a.getPrice() < b.getPrice()) return -1;	
			return 0;
		}
	}

	// Sort products alphabetically by product name
	public void printByName()
	{
		Collections.sort(tempProducts, new NameComparator());
		printAllProducts();
	}

	private class NameComparator implements Comparator<Product>
	{
		public int compare(Product a, Product b)
		{
			return a.getName().compareTo(b.getName());
		}
	}

	// Sort products alphabetically by product name
	public void sortCustomersByName()
	{
		Collections.sort(customers);
	}
	
	//Adds a product to the customer's cart
	public void addToCart(String productid, String customerID, String productOptions) throws Exception
	{
		int index = customers.indexOf(new Customer(customerID));
		if (index == -1)
		{
			custs = new UnknownCustomerException("Customer: " + customerId + " Not Found");
			throw custs;
		}
		
		Customer customer = customers.get(index);
		
		if (products.get(productid) == null)
		{
			prods = new UnknownProductException("Product " + productId + " Not Found");
			throw prods;
		}
		
		Product product = products.get(productid);

		// Check if the options are valid for this product (e.g. Paperback or Hardcover or EBook for Book product)
		if (!product.validOptions(productOptions))
		{
			invalidprods = new InvalidProductOptionsException("Product " + product.getName() + " ProductId " + productId + " Invalid Options: " + productOptions);
			throw invalidprods;
		}
		// Is it in stock?
		if (product.getStockCount(productOptions) == 0)
		{
			prodstocks = new ProductOutOfStockException("Product " + product.getName() + " ProductId " + productId + " Out of Stock");
			throw prodstocks;
		}
		
		customer.getCart().addprods(new CartItem(product, productOptions));
		
	}
	
	//Removes a product from the customer's cart
	public void removeCartItem(String customerId, String productId) throws Exception
	{
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			custs = new UnknownCustomerException("Customer: " + customerId + " Not Found");
			throw custs;
		}
		
		Customer customer = customers.get(index);
		
		if (products.get(productId) == null)
		{
			prods = new UnknownProductException("Product " + productId + " Not Found");
			throw prods;
		}
		
		Product product = products.get(productId);
		
		for (int i = 0; i < customer.getCart().getCartItems().size(); i++)
		{
			if (customer.getCart().getCartItems().get(i).getProduct().equals(product))
			{
				customer.getCart().removeprods(customer.getCart().getCartItems().get(i));
				break;
			}
		}
	}
	
	//Prints all the products in the customer's cart
	public void printCart(String customerId) throws Exception
	{
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			custs = new UnknownCustomerException("Customer: " + customerId + " Not Found");
			throw custs;
		}
		
		Customer customer = customers.get(index);
		
		for (CartItem c: customer.getCart().getCartItems())
			c.getProduct().print();
	}
	
	//Creates a product order for each product in the cart
	public void orderItems(String customerId) throws Exception
	{
		int index = customers.indexOf(new Customer(customerId));
		if (index == -1)
		{
			custs = new UnknownCustomerException("Customer: " + customerId + " Not Found");
			
			throw custs;
		}
		
		Customer customer = customers.get(index);
		
		for (int i = 0; i < customer.getCart().getCartItems().size(); i++)
		{
			String productID = customer.getCart().getCartItems().get(i).getProduct().getId();
			String productOptions = customer.getCart().getCartItems().get(i).getProductOptions();
			orderProduct(productID, customerId, productOptions);
			removeCartItem(customerId, productID);
			i--;
		}
	}
	
	// Returns true if the product type is a BOOK
	public boolean isBook(String productId) 
	{
		for (Product p : tempProducts)
		{
			if (p.getCategory() == Product.Category.BOOKS && p.getId().equals(productId))
				return true;
		}
		return false;
	}
	
	public void getStats()
	{
        // Linked List that will hold everything in the map
		LinkedList< Map.Entry<Product, Integer> > list = new LinkedList<Map.Entry< Product, Integer> > (productsStats.entrySet());
 
        // Collections sorts the linkedlist in reverse order according to the values in the map
        Collections.sort(list, new Comparator<Map.Entry<Product, Integer>>() 
        {
            public int compare(Map.Entry<Product, Integer> order1,
                               Map.Entry<Product, Integer> order2)
            {
                return (order2.getValue()).compareTo(order1.getValue());
            }
        } );
         
        // Puts data from sorted list into to linked hash map
        HashMap<Product, Integer> temp = new LinkedHashMap<Product, Integer>();
        for (Map.Entry<Product, Integer> stats : list) {
            temp.put(stats.getKey(), stats.getValue());
        }
        
		for (Map.Entry<Product, Integer> entry : temp.entrySet()) 
		{
		    System.out.printf("\nId: %-5s Name: %-20s Orders: %-5s", entry.getKey().getId(), entry.getKey().getName(), entry.getValue());
		}
	}
}

//Exception classes to print message acording to the error thrown
class UnknownCustomerException extends RuntimeException
{
	public UnknownCustomerException(String message) 
	{
		super(message);
	}
}

class UnknownProductException extends RuntimeException
{
	public UnknownProductException(String message) 
	{
		super(message);
	}
}

class InvalidCustomerAddressException extends RuntimeException
{
	public InvalidCustomerAddressException(String message) 
	{
		super(message);
	}
}

class InvalidProductOptionsException extends RuntimeException
{
	public InvalidProductOptionsException(String message) 
	{
		super(message);
	}
}

class ProductOutOfStockException extends RuntimeException
{
	public ProductOutOfStockException(String message) 
	{
		super(message);
	}
}

class InvalidCustomerNameException extends RuntimeException
{
	public InvalidCustomerNameException(String message) 
	{
		super(message);
	}
}

class InvalidOrderNumberException extends RuntimeException
{
	public InvalidOrderNumberException (String message) 
	{
		super(message);
	}
}
