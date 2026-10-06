package stockmarket

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "Price")
public class Price implements Runnable{

    @Parameters(index = '0')
    private String name; 

    private final service = new PriceService();

    @override
    public void run(){
        double price = service.getPrice(name);

        if(price == null){
            System.out.println("Unknown Stock Name");
            return;
        }
        
        System.out.printf("%.2f", price);
    } 
}