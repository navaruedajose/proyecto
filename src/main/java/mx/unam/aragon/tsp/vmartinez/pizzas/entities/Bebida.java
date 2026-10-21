package mx.unam.aragon.tsp.vmartinez.pizzas.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bebidas")
public class Bebida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoBebida tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "volumen_ml")
    private Integer volumenMl;

    @Column(nullable = false)
    private boolean disponible = true;

    public Bebida() {
    }

    public Bebida(String nombre, TipoBebida tipo, BigDecimal precio, Integer volumenMl, boolean disponible) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
        this.volumenMl = volumenMl;
        this.disponible = disponible;
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

    public TipoBebida getTipo() {
        return tipo;
    }

    public void setTipo(TipoBebida tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getVolumenMl() {
        return volumenMl;
    }

    public void setVolumenMl(Integer volumenMl) {
        this.volumenMl = volumenMl;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
