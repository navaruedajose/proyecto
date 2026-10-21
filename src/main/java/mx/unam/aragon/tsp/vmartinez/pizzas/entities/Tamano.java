package mx.unam.aragon.tsp.vmartinez.pizzas.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tamanos")
public class Tamano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nombre;

    @Column(name = "diametro_cm")
    private Integer diametroCm;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    public Tamano() {
    }

    public Tamano(String nombre, Integer diametroCm, BigDecimal precio) {
        this.nombre = nombre;
        this.diametroCm = diametroCm;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getDiametroCm() {
        return diametroCm;
    }

    public void setDiametroCm(Integer diametroCm) {
        this.diametroCm = diametroCm;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
