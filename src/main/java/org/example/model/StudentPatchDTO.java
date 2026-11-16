package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class StudentPatchDTO {

    @Size(max = 255, message = "El nombre no debe exceder los 255 caracteres")
    private String nombre;

    @Email(message = "El correo electrónico debe tener un formato válido")
    private String correo;

    @Pattern(regexp = "^\\d{10}$", message = "El número de teléfono debe contener exactamente 10 dígitos numéricos")
    @JsonProperty("numero_telefono")
    private String numeroTelefono;

    @Pattern(regexp = "^(inglés|español|francés)$", message = "El idioma solo puede ser: inglés, español o francés")
    private String idioma;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getNumeroTelefono() { return numeroTelefono; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
}