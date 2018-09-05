import org.codehaus.plexus.util.FileUtils

File privateApiFile = new File(basedir as String, "target/restdocs-spec/openapi-2.0.yml")
File publicApiFile = new File(basedir as String, "target/restdocs-spec/openapi-2.0-public.yml")

String privateApiContents = FileUtils.fileRead(privateApiFile)
String publicApiContents = FileUtils.fileRead(publicApiFile)

assert privateApiFile.exists() : 'Private spec file was not found at the expected location'
assert publicApiFile.exists() : 'Public spec file was not found at the expected location'

assert privateApiContents.contains('Add products to a cart') : 'Private spec file is missing "Add products to a cart"'
assert privateApiContents.contains('Get a cart by id') : 'Private spec file is missing "Get a cart by id"'
assert privateApiContents.contains('Order a cart') : 'Private spec file is missing "Order a cart"'
assert privateApiContents.contains('Create a cart') : 'Private spec file is missing "Create a cart"'

assert publicApiContents.contains('Add products to a cart') : 'Public spec file is missing "Add products to a cart"'
assert publicApiContents.contains('Get a cart by id') : 'Public spec file is missing "Get a cart by id"'
assert !publicApiContents.contains('Order a cart') : 'Public spec file should not contain "Order a cart"'
assert !publicApiContents.contains('Create a cart') : 'Public spec file should not contain "Create a cart"'

