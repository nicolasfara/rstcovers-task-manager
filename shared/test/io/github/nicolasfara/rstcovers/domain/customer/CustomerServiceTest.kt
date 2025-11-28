package io.github.nicolasfara.rstcovers.domain.customer

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CustomerServiceTest {
    private val johnDoe = Customer(
        id = CustomerId(),
        name = CustomerName("John Doe"),
        customerType = CustomerType.INDIVIDUAL,
        email = Email("john@doe.com"),
        cellPhone = CellPhone("+3-123456"),
        address = Address("street 123"),
        fiscalCode = FiscalCode("ABCDEF12G34H567I"),
    )
    @Test
    fun `create a customer with an already register email should result in a 'CustomerAlreadyExists'`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        repository.save(johnDoe)
        val result = service.createCustomer(
            name = CustomerName("John Doe"),
            type = CustomerType.INDIVIDUAL,
            email = Email("john@doe.com"),
            cellPhone = CellPhone("+3-123456"),
            address = Address("street 123"),
            fiscalCode = FiscalCode("ABCDEF12G34H567I"),
        )
        assertTrue(result.isLeft(), "Expected error when creating a customer with an already registered email")
        val error = result.swap().getOrNull()
        assertIs<CustomerError.CustomerAlreadyExists>(error!!)
        assertEquals(error.name, CustomerName("John Doe"))
    }

    @Test
    fun `create a customer with an already register fiscal code should result in a 'CustomerAlreadyExists'`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        repository.save(johnDoe)
        val result = service.createCustomer(
            name = CustomerName("John Doe"),
            type = CustomerType.INDIVIDUAL,
            email = Email("john@doe.com"),
            cellPhone = CellPhone("+3-123456"),
            address = Address("street 123"),
            fiscalCode = FiscalCode("ABCDEF12G34H567I"),
        )
        assertTrue(result.isLeft(), "Expected error when creating a customer with an already registered fiscal code")
        val error = result.swap().getOrNull()
        assertIs<CustomerError.CustomerAlreadyExists>(error!!)
        assertEquals(error.name, CustomerName("John Doe"))
    }

    @Test
    fun `create a new customer with valid data should succeed`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        val result = service.createCustomer(
            name = CustomerName("Jane Doe"),
            type = CustomerType.INDIVIDUAL,
            email = Email("jane@doe.com"),
            cellPhone = CellPhone("+3-654321"),
            address = Address("avenue 456"),
            fiscalCode = FiscalCode("ZYXWVU98T76S543R"),
        )
        assertTrue(result.isRight(), "Expected successful customer creation")
        val customerId = result.getOrNull()
        assertIs<CustomerId>(customerId!!)
        val savedCustomer = repository.findById(customerId).getOrNull()
        assertIs<Customer>(savedCustomer!!)
        assertEquals(savedCustomer.name, CustomerName("Jane Doe"))
        assertEquals(savedCustomer.email, Email("jane@doe.com"))
        assertEquals(savedCustomer.fiscalCode, FiscalCode("ZYXWVU98T76S543R"))
    }

    @Test
    fun `get an existing customer by ID should return the customer`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        repository.save(johnDoe)
        val result = service.getCustomer(johnDoe.id)
        assertTrue(result.isRight(), "Expected successful retrieval of existing customer")
        val customer = result.getOrNull()
        assertIs<Customer>(customer!!)
        assertEquals(customer.name, johnDoe.name)
    }

    @Test
    fun `get a non-existing customer by ID should return an 'CustomerNotFound'`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        val result = service.getCustomer(CustomerId())
        assertTrue(result.isLeft(), "Expected successful retrieval with null for non-existing customer")
        val error = result.swap().getOrNull()
        assertIs<CustomerError.CustomerNotFound>(error!!)
    }

    @Test
    fun `update an existing customer should succeed`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        repository.save(johnDoe)
        val updatedCustomer = johnDoe.copy(name = CustomerName("Johnathan Doe"))
        val updateResult = service.updateCustomer(updatedCustomer)
        assertTrue(updateResult.isRight(), "Expected successful update of existing customer")
        val fetchedCustomer = repository.findById(johnDoe.id).getOrNull()
        assertIs<Customer>(fetchedCustomer!!)
        assertEquals(fetchedCustomer.name, CustomerName("Johnathan Doe"))
    }

    @Test
    fun `update a non-existing customer should return 'CustomerNotFound'`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        val nonExistingCustomer = Customer(
            id = CustomerId(),
            name = CustomerName("Non Existing"),
            customerType = CustomerType.INDIVIDUAL,
            email = Email("non@existing.com"),
            cellPhone = CellPhone("+3-000000"),
            address = Address("nowhere 0"),
            fiscalCode = FiscalCode("NONEXIST1234567"),
        )
        val updateResult = service.updateCustomer(nonExistingCustomer)
        assertTrue(updateResult.isLeft(), "Expected error when updating a non-existing customer")
        val error = updateResult.swap().getOrNull()
        assertIs<CustomerError.CustomerNotFound>(error!!)
    }

    @Test
    fun `delete an existing customer should succeed`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        repository.save(johnDoe)
        val deleteResult = service.deleteCustomer(johnDoe.id)
        assertTrue(deleteResult.isRight(), "Expected successful deletion of existing customer")
        val fetchedCustomer = repository.findById(johnDoe.id).getOrNull()
        assertEquals(fetchedCustomer, null)
    }

    @Test
    fun `delete a non-existing customer should return 'CustomerNotFound'`() = runTest {
        val repository = CustomerRepositoryMock()
        val service = CustomerService(repository)
        val deleteResult = service.deleteCustomer(CustomerId())
        assertTrue(deleteResult.isLeft(), "Expected error when deleting a non-existing customer")
        val error = deleteResult.swap().getOrNull()
        assertIs<CustomerError.CustomerNotFound>(error!!)
    }
}