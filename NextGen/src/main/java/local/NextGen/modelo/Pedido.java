package local.NextGen.modelo;

import local.NextGen.modelo.DAO.DetallePedidoDAO;

import javax.persistence.*;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase que representa un pedido realizado por un cliente.
 * Incluye información sobre el pedido, como el número de pedido, la fecha y hora,
 * el cliente que realizó el pedido y los detalles del mismo.
 */

@Entity
@Table(name = "pedidos")
public class Pedido implements Serializable {
    public enum EstadoPedido {
        PENDIENTE,
        ENVIADO
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_pedido")
    private int numeroPedido;

    @Column(name = "fecha_hora_pedido")
    private Date fechaHora;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pedido")
    private EstadoPedido estadoPedido;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detallesPedido;

    public Pedido() {
    }

    /**
     * Constructor para crear un nuevo pedido.
     * @param numeroPedido Número del pedido.
     * @param fechaHora Fecha y hora del pedido.
     * @param cliente Cliente que realiza el pedido.
     * @param detallesPedido Lista de detalles del pedido.
     */
    public Pedido(int numeroPedido, Date fechaHora, Cliente cliente, List<DetallePedido> detallesPedido, EstadoPedido estadoPedido) {
        this.numeroPedido = numeroPedido;
        this.fechaHora = fechaHora;
        this.cliente = cliente;
        this.detallesPedido = detallesPedido;
        this.estadoPedido = estadoPedido;
    }

    // Getters y setters
    public int getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(int numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public List<DetallePedido> getDetallesPedido() {
        return detallesPedido;
    }

    public void setDetallesPedido(List<DetallePedido> detallesPedido) {
        this.detallesPedido = detallesPedido;
    }

    /**
     * Calcula el precio total del pedido sumando los precios de todos los detalles.
     * @return Precio total del pedido.
     */
    public Double precioTotal() {
        Double total = 0.0;
        DetallePedidoDAO dpd = new DetallePedidoDAO();
        List<DetallePedido> pedidoDetalles = dpd.listarPorPedido(this);
        for (DetallePedido detalle : pedidoDetalles) {
            total += detalle.getPrecioVenta() * detalle.getCantidad();
        }
        return total;
    }
    /**
     * Agrega un detalle al pedido.
     *
     * @param detalle El detalle a agregar.
     */
    public void agregarDetalle(DetallePedido detalle) {
        if (detallesPedido == null) {
            detallesPedido = new ArrayList<>();
        }
        detallesPedido.add(detalle);
    }

    /**
     * Devuelve una representación en cadena del pedido, incluyendo todos los detalles.
     * @return Representación en cadena del pedido.
     */
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String fechaFormato = dateFormat.format(fechaHora);


        StringBuilder detalles = new StringBuilder();

        DetallePedidoDAO dpd = new DetallePedidoDAO();
        List<DetallePedido> pedidoDetalles = dpd.listarPorPedido(this);
        detalles.append("\n\u001B[33mDetalles del Pedido:\u001B[0m\n");
        for (DetallePedido detalle : pedidoDetalles) {
            detalles.append(detalle.toString()).append("\n");
        }



        return String.format("Pedido Número: %-5s| Fecha: %-10s | Cliente: %-10s | Estado: %-8s | Precio Total: %-8s %s",
                numeroPedido, fechaFormato, cliente.getNif(), estadoPedido, df.format(precioTotal()) + "€", detalles);
    }
}
