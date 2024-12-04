package it.niedermann.nextcloud.tables.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.tables.database.entity.Account;

@Dao
public interface AccountDao extends GenericDao<Account> {

    @Query("SELECT * FROM Account")
    LiveData<List<Account>> getAccounts$();

    @Query("SELECT * FROM Account")
    List<Account> getAccounts();

    @Query("SELECT * FROM Account WHERE id = :accountId")
    LiveData<Account> getAccountById$(long accountId);

    @Query("SELECT * FROM Account WHERE id = :accountId")
    Account getAccountById(long accountId);

    @Query("SELECT * FROM Account WHERE id != :id")
    LiveData<List<Account>> getAccountsExcept$(long id);

    @Query("UPDATE Account " +
            "SET currentTable = (" +
            "   SELECT t.id " +
            "   FROM `Table` t " +
            "   WHERE t.accountId = :accountId " +
            "   AND t.status != 'LOCAL_DELETED' " +
            "   ORDER BY t.lastEditAt " +
            "   LIMIT 1" +
            ") " +
            "WHERE Account.id = :accountId " +
            "AND Account.currentTable IS NULL")
    void guessCurrentTable(long accountId);

    @Query("UPDATE Account SET currentTable = :tableId WHERE id = :id")
    void updateCurrentTable(long id, Long tableId);
}
