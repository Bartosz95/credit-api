package pl.inteca.product.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import pl.inteca.product.api.ProductRepoApi;
import pl.inteca.product.domain.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Configuration
@NoArgsConstructor
@PropertySource("classpath:application.properties")
public class ProductRepository implements ProductRepoApi {

    /*
     * values with annotation @Value are inside file /src/main/resources/application.properties
     */
    @Value("${database.url}")
    private String url;
    @Value("${database.user}")
    private String user;
    @Value("${database.password}")
    private String password;
    @Value("${database.driver}")
    private String driver;

    /*
     * Function apply driver for mysql.
     * Is possible change driver in resource/application.properties
     * IN: -
     * OUT: -
     */
    @Autowired
    public void applyDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e){
            System.out.println(String.format("Error with driver: ", e.getMessage()));
        }
    }

    /*
     * Function create table "product" in database
     * table product have (id, name, value)
     * IN: -
     * OUT -
     */
    @Autowired
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS products (" +
                "id BIGINT NOT NULL AUTO_INCREMENT," +
                "name VARCHAR(255)," +
                "value BIGINT," +
                "PRIMARY KEY (id))";
        try(Connection connection = DriverManager.getConnection(url,user,password);
            Statement statement = connection.createStatement()){
            statement.executeUpdate(query);
        } catch (SQLException e){
            System.out.println(String.format("Database server not response. Error: \n %s", e.getMessage()));
        }
    }

    /*
     * Function save product in credit-db.product table
     * IN: Product object without id, database connection
     * OUT: The same Product object with id
     */
    @Override
    public Product createProduct(Product product) {
        try (Connection connection = DriverManager.getConnection(url,user,password);
             Statement statement = connection.createStatement()) {
            // insert product into table
            // id is autoincrement
            String query = String.format("INSERT INTO products (name, value) " +
                    "VALUES ('%s', '%d' )", product.getName(), product.getValue());
            statement.executeUpdate(query);
            query = "SELECT LAST_INSERT_ID() AS id FROM products"; // get new product id
            ResultSet result = statement.executeQuery(query);
            if(result.next())
                product.setId(result.getLong("id"));
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return product;
    }

    /*
     * Function return all products form table products
     * It send request to database looks like:
     * SELECT * FROM products WHERE id IN(1,2,3,4)
     * and database send response like result list.
     * IN: List of id in long value, database connection
     * OUT: List of Product object
     */
    @Override
    public List<Product> getProducts(List<Long> idList) {
        // Prepare query string
        StringBuilder query = new StringBuilder("SELECT * FROM products WHERE id IN(");
        for(Long id : idList){
            query.append(id);
            query.append(",");
        }
        query.deleteCharAt(query.lastIndexOf(","));
        query.append(")");

        List<Product> products = new ArrayList<>();
        // Send query to database
        try(Connection connection = DriverManager.getConnection(url,user,password);
            Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query.toString());
            while (result.next()) {
                products.add(Product.builder() // create product by Builder and add to list
                        .id(result.getLong("id"))
                        .name(result.getString("name"))
                        .value(result.getLong("value"))
                        .build());
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return products;
    }
}