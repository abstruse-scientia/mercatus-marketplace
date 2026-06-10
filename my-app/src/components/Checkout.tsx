import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useCartStore } from "../store/cartStore";
import { addressesApi } from "../api/addresses.api";
import { ordersApi } from "../api/orders.api";
import { ChevronRight } from "lucide-react";
import toast from "react-hot-toast";
import type { Address } from "../types";

export default function Checkout() {
  const { cart, isLoading: cartLoading } = useCartStore();
  const navigate = useNavigate();

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [selectedAddressId, setSelectedAddressId] = useState<number | null>(null);
  const [showNewAddressForm, setShowNewAddressForm] = useState(false);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    city: "",
    state: "",
    zipCode: "",
    description: "",
  });

  // Redirect if cart is empty
  useEffect(() => {
    if (!cartLoading && (!cart || cart.itemCount === 0)) {
      navigate("/cart");
    }
  }, [cart, cartLoading, navigate]);

  // Load existing addresses
  useEffect(() => {
    const fetchAddress = async () => {
      try {
        const addrList = await addressesApi.getAddresses();
        if (addrList && addrList.length > 0) {
          setAddresses(addrList);
          setSelectedAddressId(addrList[0].addressId);
        } else {
          setShowNewAddressForm(true);
        }
      } catch (error) {
        console.error("Failed to load addresses", error);
        setShowNewAddressForm(true);
      }
    };
    fetchAddress();
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCheckout = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (isSubmitting) return;

    try {
      setIsSubmitting(true);
      
      let finalAddressId = selectedAddressId;

      if (showNewAddressForm) {
        // 1. Create Address
        const addressPayload = {
          isDefault: true,
          fullName: `${formData.firstName} ${formData.lastName}`.trim(),
          mobileNumber: formData.phoneNumber,
          flatHouse: formData.description,
          area: "N/A", // Placeholder as per backend requirements
          pincode: formData.zipCode,
          city: formData.city,
          state: formData.state,
          country: "India",
        };

        const addressResponse = await addressesApi.addAddress(addressPayload);
        finalAddressId = addressResponse.addressId;
      }

      if (!finalAddressId) {
        toast.error("Please select or add a shipping address");
        setIsSubmitting(false);
        return;
      }

      // 2. Place Order
      const orderReference = `ORD-${Date.now()}-${Math.floor(Math.random() * 1000)}`;
      const orderResponse = await ordersApi.placeOrder({
        orderReference,
        addressId: finalAddressId,
      });

      // 3. Clear cart (done automatically by backend checkout, but we update frontend state)
      useCartStore.getState().fetchCart();
      toast.success("Order placed successfully!");
      
      // 4. Redirect to order confirmation
      navigate(`/orders/${orderResponse.orderId}`, { state: { order: orderResponse } });

    } catch (error: unknown) {
      const err = error as any;
      toast.error(err?.response?.data?.errorMessage || "Failed to place order");
      setIsSubmitting(false);
    }
  };

  if (!cart) return null;

  const shippingCost = 9.00;
  const estimatedTaxes = 5.00;
  const total = cart.subtotal + shippingCost + estimatedTaxes;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      
      <div className="flex flex-col lg:flex-row gap-12">
        {/* Left Column: Form / Addresses */}
        <div className="flex-1">
          {/* Breadcrumbs */}
          <nav className="flex items-center text-sm text-muted-foreground mb-8 space-x-2">
            <Link to="/cart" className="hover:text-foreground text-blue-600 font-medium">Cart</Link>
            <ChevronRight className="w-4 h-4" />
            <span className="text-foreground font-medium">Shipping</span>
            <ChevronRight className="w-4 h-4" />
            <span>Payment</span>
          </nav>

          {/* Header */}
          <div className="flex items-center gap-4 mb-6">
            <div className="w-7 h-7 rounded-full bg-foreground text-background flex items-center justify-center font-bold text-sm">
              1
            </div>
            <h1 className="text-sm font-bold text-muted-foreground uppercase tracking-widest">Shipping Destination</h1>
          </div>

          <div className="border border-border rounded-xl overflow-hidden mb-8">
            {addresses.map((addr, idx) => (
              <div 
                key={addr.addressId}
                className={`p-6 border-b border-border cursor-pointer flex items-start gap-4 transition-colors ${selectedAddressId === addr.addressId && !showNewAddressForm ? 'bg-muted/50' : 'bg-background hover:bg-muted/30'}`}
                onClick={() => {
                  setSelectedAddressId(addr.addressId);
                  setShowNewAddressForm(false);
                }}
              >
                <div className="pt-1">
                  <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${selectedAddressId === addr.addressId && !showNewAddressForm ? 'border-foreground' : 'border-border'}`}>
                    {selectedAddressId === addr.addressId && !showNewAddressForm && (
                      <div className="w-2.5 h-2.5 bg-foreground rounded-full" />
                    )}
                  </div>
                </div>
                <div className="flex-1">
                  <h3 className="font-semibold text-foreground text-lg mb-1">{addr.fullName}</h3>
                  <p className="text-muted-foreground text-sm mb-1">{addr.flatHouse}, {addr.area !== "N/A" ? addr.area : ""}</p>
                  <p className="text-muted-foreground text-sm">{addr.city}, {addr.pincode} — {addr.country}</p>
                </div>
                {idx === 0 && (
                   <span className="px-3 py-1 bg-background border border-border text-muted-foreground text-xs font-bold uppercase rounded tracking-wider">Active</span>
                )}
                {idx === 1 && (
                   <span className="px-3 py-1 bg-background border border-border text-muted-foreground text-xs font-bold uppercase rounded tracking-wider">Office</span>
                )}
              </div>
            ))}
            
            <div 
              className={`p-6 cursor-pointer flex items-center gap-4 transition-colors ${showNewAddressForm ? 'bg-muted/50' : 'bg-background hover:bg-muted/30'}`}
              onClick={() => {
                 setShowNewAddressForm(true);
                 setSelectedAddressId(null);
              }}
            >
              <div className="text-muted-foreground text-xl font-light ml-1">+</div>
              <span className="font-semibold text-muted-foreground">New Destination Record</span>
            </div>
          </div>

          {showNewAddressForm && (
            <div className="animate-in fade-in slide-in-from-top-4 duration-300">
              <h2 className="text-lg font-bold text-foreground mb-6">Add New Address</h2>
              <form id="checkout-form" onSubmit={handleCheckout} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-foreground mb-1">First Name*</label>
                    <input
                      required
                      type="text"
                      name="firstName"
                      value={formData.firstName}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:ring-foreground focus:border-foreground"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Last Name*</label>
                    <input
                      required
                      type="text"
                      name="lastName"
                      value={formData.lastName}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:ring-foreground focus:border-foreground"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Email*</label>
                    <input
                      required
                      type="email"
                      name="email"
                      value={formData.email}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:ring-foreground focus:border-foreground"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Phone number*</label>
                    <div className="flex">
                      <span className="inline-flex items-center px-4 rounded-l-lg border border-r-0 border-border bg-muted/50 text-muted-foreground sm:text-sm">
                        IND
                      </span>
                      <input
                        required
                        type="tel"
                        name="phoneNumber"
                        value={formData.phoneNumber}
                        onChange={handleInputChange}
                        placeholder="+91"
                        className="flex-1 min-w-0 block w-full px-4 py-2 rounded-none rounded-r-lg border border-border bg-background text-foreground focus:ring-foreground focus:border-foreground"
                      />
                    </div>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-foreground mb-1">City*</label>
                    <input
                      required
                      type="text"
                      name="city"
                      value={formData.city}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:ring-foreground focus:border-foreground"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-foreground mb-1">State*</label>
                    <input
                      required
                      type="text"
                      name="state"
                      value={formData.state}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:ring-foreground focus:border-foreground"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Zip Code*</label>
                    <input
                      required
                      type="text"
                      name="zipCode"
                      value={formData.zipCode}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:ring-foreground focus:border-foreground"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-foreground mb-1">Description*</label>
                  <textarea
                    required
                    name="description"
                    rows={3}
                    value={formData.description}
                    onChange={handleInputChange}
                    placeholder="Enter a description (Flat, House no., Building, Company, Apartment)"
                    className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:ring-foreground focus:border-foreground"
                  />
                </div>
              </form>
            </div>
          )}
        </div>

        {/* Right Column: Order Summary */}
        <div className="w-full lg:w-[450px]">
          <div className="bg-muted/30 rounded-2xl p-6 lg:p-8 sticky top-24">
            <h2 className="text-2xl font-bold text-foreground mb-6">Your Cart</h2>
            
            {/* Items */}
            <div className="space-y-4 mb-6 max-h-64 overflow-y-auto pr-2">
              {cart.items.map((item) => (
                <div key={item.productId} className="flex items-center gap-4">
                  <div className="relative">
                    <img 
                      src={item.primaryImageUrl || "https://placehold.co/100x100"} 
                      alt={item.name} 
                      className="w-16 h-16 rounded-lg object-cover border border-border"
                    />
                    <span className="absolute -top-2 -right-2 bg-foreground text-background text-xs font-bold w-5 h-5 rounded-full flex items-center justify-center">
                      {item.quantity}
                    </span>
                  </div>
                  <div className="flex-1">
                    <h3 className="font-medium text-foreground text-sm">{item.name}</h3>
                    <p className="text-muted-foreground text-xs">{item.categoryName}</p>
                  </div>
                  <p className="font-medium text-foreground">${(item.unitPrice * item.quantity).toFixed(2)}</p>
                </div>
              ))}
            </div>

            {/* Discount Code */}
            <div className="flex gap-2 mb-6">
              <div className="relative flex-1">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <span className="text-muted-foreground font-bold">%</span>
                </div>
                <input
                  type="text"
                  placeholder="Discount code"
                  className="w-full pl-10 pr-4 py-2.5 border border-border rounded-lg bg-background focus:ring-foreground focus:border-foreground text-sm"
                />
              </div>
              <button type="button" className="px-5 py-2.5 bg-background border border-border text-foreground font-medium rounded-lg hover:bg-muted/50 text-sm transition-colors">
                Apply
              </button>
            </div>

            {/* Totals */}
            <div className="space-y-3 text-sm text-muted-foreground mb-6 border-t border-border pt-6">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <span className="font-medium text-foreground">${cart.subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Shipping</span>
                <span className="font-medium text-foreground">${shippingCost.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Estimated taxes <span className="text-muted-foreground/50 ml-1">ⓘ</span></span>
                <span className="font-medium text-foreground">${estimatedTaxes.toFixed(2)}</span>
              </div>
            </div>

            <div className="flex justify-between items-center mb-8 border-t border-border pt-6">
              <span className="text-lg font-bold text-foreground">Total</span>
              <span className="text-2xl font-bold text-foreground">${total.toFixed(2)}</span>
            </div>

            <button
              onClick={showNewAddressForm ? undefined : () => handleCheckout()}
              form={showNewAddressForm ? "checkout-form" : undefined}
              type={showNewAddressForm ? "submit" : "button"}
              disabled={isSubmitting || (!showNewAddressForm && !selectedAddressId)}
              className="w-full bg-foreground text-background font-medium py-4 rounded-lg hover:bg-foreground/90 transition-colors disabled:opacity-70 disabled:cursor-not-allowed flex justify-center items-center"
            >
              {isSubmitting ? (
                <div className="w-5 h-5 border-2 border-background border-t-transparent rounded-full animate-spin"></div>
              ) : (
                "Continue to Payment"
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
