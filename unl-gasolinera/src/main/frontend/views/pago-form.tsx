import React, { useState, useEffect } from 'react';
import { PagoService } from 'Frontend/generated/endpoints';

export default function PagoForm() {
  const [checkoutId, setCheckoutId] = useState<string | null>(null);

  const iniciarPago = async () => {
    const resp = await PagoService.checkout(10.00, 'USD');
    if (resp && resp.id) {
      setCheckoutId(resp.id);
    } else {
      alert('No se pudo iniciar el pago');
    }
  };

  useEffect(() => {
    if (checkoutId) {
      const script = document.createElement('script');
      script.src = `https://eu-test.oppwa.com/v1/paymentWidgets.js?checkoutId=${checkoutId}`;
      script.async = true;
      document.body.appendChild(script);
      return () => {
        document.body.removeChild(script);
      };
    }
  }, [checkoutId]);

  return (
    <div>
      {!checkoutId && (
        <button onClick={iniciarPago}>Pagar</button>
      )}
      {checkoutId && (
        <form
          action="/confirmacion" // Cambia por tu endpoint de confirmación
          className="paymentWidgets"
          data-brands="VISA MASTER AMEX"
        ></form>
      )}
    </div>
  );
}