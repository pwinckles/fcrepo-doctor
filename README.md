# fcrepo-doctor

A tool for diagnosing and repairing OCFL-based Fedora repositories.

## Usage

```
Usage: fcrepo-doctor [-dhvV] [-o=<outputDir>] [-p=<parallelism>] -r=<ocflRoot>
                     [--s3-access-key=<s3AccessKey>] [--s3-bucket=<s3Bucket>]
                     [--s3-endpoint=<s3Endpoint>] [--s3-profile=<s3Profile>]
                     [--s3-region=<s3Region>] [--s3-secret-key=<s3SecretKey>]
                     -t=<tempDir>
A tool for diagnosing and repairing OCFL-based Fedora repositories.
  -d, --debug                Enable stack traces
  -h, --help                 Show this help message and exit.
  -o, --output=<outputDir>   Path to a directory to write output files into.
                               Default: current directory
  -p, --parallelism=<parallelism>
                             Number of threads to use. Default: number of cores
                               minus one
  -r, --ocfl-root=<ocflRoot> Path to Fedora's OCFL storage root. When using S3,
                               this is the prefix within the bucket that the
                               storage root is located it, and it should be an
                               empty string if no prefix is used.
      --s3-access-key=<s3AccessKey>
                             S3 access key. If provided, a secret key must also
                               be specified
      --s3-bucket=<s3Bucket> S3 bucket the OCFL repository is in
      --s3-endpoint=<s3Endpoint>
                             URL to the S3 endpoint
      --s3-profile=<s3Profile>
                             S3 profile to use.
      --s3-region=<s3Region> S3 region
      --s3-secret-key=<s3SecretKey>
                             S3 secret key. If provided, an access key must
                               also be specified
  -t, --temp=<tempDir>       Path to a directory on the same filesystem as the
                               OCFL root to use for temporary files
  -v, --verbose              Enable more verbose logging
  -V, --version              Print version information and exit.
```

## Problems

### Invalid Binary Description RDF Subjects

Versions of Fedora 6 prior to 6.2.0 allowed adding triples to a binary description that used the binary description's
resource ID as the subject rather than the binary itself, and it would serialize this subject directly rather than
translating it to be the binary's ID. However, this was a bug, and it should have translated the ID. `fcrepo-doctor`
is able to identify any binary descriptions that contain invalid subjects, and correct them.
