### General Information

Tests will automatically generate and execute structure tests for the target project,
make sure that the `test.json` file exists in the root package.

If the tests are using `Ares`, make sure to add the `structure` package in the trusted packages list.
You can do so by adding the following line to your meta annotation interface - `@AddTrustedPackage({"**structure**"})`.