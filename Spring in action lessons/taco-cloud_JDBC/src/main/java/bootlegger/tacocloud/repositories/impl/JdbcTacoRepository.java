package bootlegger.tacocloud.repositories.impl;

import bootlegger.tacocloud.model.Ingredient;
import bootlegger.tacocloud.model.Taco;
import bootlegger.tacocloud.repositories.TacoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

@Repository
public class JdbcTacoRepository implements TacoRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTacoRepository (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Taco save (Taco taco) {
        taco.setId(saveTacoInfo(taco));
        for(Ingredient ingredient : taco.getIngredients()) {
            saveIngredientToTaco(ingredient, taco.getId());
        }
        return taco;
    }

    private long saveTacoInfo (Taco taco) {
        taco.setCreateAt(new Date());
        PreparedStatementCreatorFactory preparedStatementCreatorFactory = new PreparedStatementCreatorFactory(
                "INSERT INTO taco (name, createAt) values(?, ?)", Types.VARCHAR, Types.TIMESTAMP
        );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);
        PreparedStatementCreator preparedStatementCreator = preparedStatementCreatorFactory.newPreparedStatementCreator(
                Arrays.asList(taco.getName(), new Timestamp(taco.getCreateAt().getTime())));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private void saveIngredientToTaco (Ingredient ingredient, long tacoId) {
        jdbcTemplate.update("INSERT INTO taco_ingredients (taco, ingredient) values (?, ?)", tacoId,
                            ingredient.getId());
    }
}
