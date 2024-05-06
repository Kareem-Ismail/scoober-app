package com.justeattakeaway.codechallenge;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class ExampleH2DB {

    @Autowired
    DummyTableRepository repository;

    public void testRepository() {
        repository.deleteAll();
        repository.save(new DummyTable("test-data"));
        repository.findAll().forEach(System.out::println);
    }

}

@Repository
interface DummyTableRepository extends JpaRepository<DummyTable, Long> {}

@Entity
class DummyTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String data;

    public DummyTable() {}

    public DummyTable(String data) {
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
