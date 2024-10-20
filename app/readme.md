Proposed Firestore Structure Recap
1. Users Collection
   Document ID: userId
   Fields:
   name: string
   email: string
   profileImageUrl: string
   gender: string
   age: integer
   language: string
   settings:
   notifications: boolean
   darkMode: boolean
   favorites: array (references to favorite products)
   cart: array (objects with productId, quantity, size, color)
   orders: array (references to orders)
   addresses: array
   country: string
   state: string
   city: string
   zipCode: string
   streetAddress: string
2. Categories Collection
   Document ID: categoryId
   Fields:
   name: string
   image: string (URL to the category image)
   productCount: integer (number of products in the category)
3. Products Collection
   Document ID: productId
   Fields:
   name: string
   category: reference (link to a document in the Categories collection)
   price: float
   description: string
   imageUrls: array (URLs to product images)
   sizes: array (e.g., ["S", "M", "L"])
   colors: array (e.g., ["#000000", "#FFFFFF"])
   reviews: integer
   rating: float
   available: boolean
   timestamp: timestamp (for sorting new arrivals)
4. Orders Collection
   Document ID: orderId
   Fields:
   userId: reference (link to a document in the Users collection)
   productIds: array (references to products in the Products collection)
   totalPrice: float
   status: string (e.g., "Shipped", "Delivered")
   collectionPoint:
   address: string
   time: timestamp
   history: array (contains objects with fields like date, status, location)
5. Messages Collection
   Document ID: messageId
   Fields:
   senderId: reference (link to a document in the Users collection)
   receiverId: reference (link to a document in the Users collection)
   message: string
   timestamp: timestamp
   isRead: boolean